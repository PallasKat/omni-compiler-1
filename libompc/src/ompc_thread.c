/* 
 * $TSUKUBA_Release: Omni OpenMP Compiler 3 $
 * $TSUKUBA_Copyright:
 *  PLEASE DESCRIBE LICENSE AGREEMENT HERE
 *  $
 *
 * @file ompc_thread.c
 */

#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include "exc_platform.h"
#include "ompclib.h"

#define PROC_HASH_SIZE  0x100L
#define PROC_HASH_MASK (PROC_HASH_SIZE-1)
#define PROC_HASH_IDX(ID) ((unsigned long int)((unsigned long int)(ID) & (PROC_HASH_MASK)))

#define DEF_STACK_SIZE  1*1024*1024     /* default stack size */

int ompc_debug_flag = 0;       /* debug output control */
int ompc_log_flag = 0;         /* log */

volatile int ompc_nested;      /* nested enable/disable */
volatile int ompc_dynamic;     /* dynamic enable/disable */
volatile int ompc_task_end;    /* slave task end */
volatile int ompc_proc_counter = 0;    /* thread generation counter */

volatile int ompc_max_threads; /* max number of thread */
volatile int ompc_num_threads; /* number of team member? */

int ompc_n_proc = N_PROC_DEFAULT;      /* number of PE */
int ompc_bind_procs = FALSE;   /* default */

/* system lock variables */
ompc_lock_t ompc_proc_lock_obj, ompc_thread_lock_obj;

ABT_mutex ompc_proc_mutex;
ABT_cond ompc_proc_cond;
ABT_cond ompc_mainwait_cond;
ABT_mutex ompc_mainwait_mutex;

/* hash table */
struct ompc_proc *ompc_proc_htable[PROC_HASH_SIZE];
/* proc table */
struct ompc_proc *ompc_procs;

static ompc_proc_t ompc_master_proc_id;

/* prototype */
static void *ompc_slave_proc(void *);
static void ompc_xstream_setup();
static void ompc_thread_wrapper_func(void *args);
static struct ompc_proc *ompc_new_proc(void);
static struct ompc_proc *ompc_current_proc(void);
static struct ompc_proc *ompc_get_proc();
static void ompc_free_proc(struct ompc_proc *p);
static struct ompc_thread *ompc_alloc_thread(struct ompc_proc *proc);
static void ompc_free_thread(struct ompc_proc *proc, struct ompc_thread *p);
/*static*/ struct ompc_thread *ompc_current_thread(void);
static void ompc_thread_barrier2(int id, struct ompc_thread *tpp);

extern void ompc_call_fsub(struct ompc_thread *tp);

/* 
 * initialize library
 */
#ifdef not
void
ompc_init_proc_num(int pnum)
{
    ompc_n_proc = pnum;
    ompc_init();
}
#endif

void
ompc_init(int argc,char *argv[])
{
    char  * cp;
    int t, r, val;
    long lnp;
    struct ompc_thread *tp;
    struct ompc_proc *cproc;
    size_t maxstack = 0;

    static ABT_xstream xstreams[MAX_PROC];
    ABT_init(argc, argv);
    ABT_mutex_create(&ompc_proc_mutex);
    ABT_cond_create(&ompc_proc_cond);
    ABT_mutex_create(&ompc_mainwait_mutex);
    ABT_cond_create(&ompc_mainwait_cond);

    {
      char buff[BUFSIZ];
      FILE *fp;
      int  npes;
      char procfile[] = "/proc/stat";

      fp = fopen (procfile, "r");
      if ( fp == NULL ){
        fprintf (stderr, "cannot open \"%s\".\n"
                 "cannot get maximum number of processors.\n", procfile);
        lnp = 1;
      }
      else {
        npes = 0;
        while( fgets(buff, BUFSIZ, fp) != NULL ){
          if ( !strncmp(buff, "cpu", 3) && isdigit(buff[3]) ){
            npes += 1;
          }
        }
        fclose (fp);
        lnp = (npes == 0)? 1: npes;
      }
    }

    if (ompc_n_proc != lnp)
        ompc_n_proc = lnp;
    
    atexit(ompc_finalize);

    cp = getenv("OMPC_DEBUG");
    if(cp != NULL){
        ompc_debug_flag = TRUE;
        fprintf(stderr,"debug flag on ...\n");
    }

#ifdef USE_LOG
    cp = getenv("OMPC_LOG");
    if(cp != NULL){
        ompc_log_flag = TRUE;
        tlog_init(argv[0]);
    }
#endif /* USE_LOG */

    cp = getenv("OMPC_BIND_PROCS");
    if(cp != NULL && (strcmp(cp, "TRUE") == 0 || strcmp(cp, "true") == 0))
        ompc_bind_procs = TRUE;

    cp = getenv("OMP_SCHEDULE");
    if(cp != NULL)
        ompc_set_runtime_schedule(cp);

    cp = getenv("OMP_DYNAMIC");
    if(cp != NULL && (strcmp(cp, "TRUE") == 0 || strcmp(cp, "true") == 0))
        ompc_dynamic = 1;
    else
        ompc_dynamic = 0;      /* dynamic enable/disable */

    cp = getenv("OMP_NESTED");
    if(cp != NULL && (strcmp(cp,"TRUE") == 0 || strcmp(cp,"true") == 0))
        ompc_nested = 1;
    else
        ompc_nested = 0;       /* nested enable/disable */

    cp = getenv("OMPC_NUM_PROCS");   /* processor */
    if ( cp != NULL ){
        sscanf(cp, "%d", &val);
        if(val <= 0) ompc_fatal("bad OMPC_NUM_PROCS(<= 0)");
        ompc_n_proc = val;
    }

    ompc_max_threads = ompc_n_proc;   /* max number of thread, default */

    cp = getenv("OMP_NUM_THREADS");     /* a number of team member */
    if ( cp == NULL )
        ompc_num_threads = ompc_n_proc;
    else {
        sscanf(cp, "%d", &val);
        if(val <= 0) ompc_fatal("bad OMP_NUM_THREADS(<= 0)");
        ompc_num_threads = val;

        if(ompc_num_threads > ompc_max_threads)
            ompc_max_threads = ompc_num_threads;
    }

    /* ompc_num_threads cannot be different from
     * ompc_max_threads in Omni. 
     */
    ompc_max_threads = ompc_num_threads;

    cp = getenv("OMPC_STACK_SIZE");   /* stack size of threads */
    if ( cp != NULL ){
        char lstr[64];
        size_t len = strlen(cp);
        int unit = 1;

        if ( strncmp(&cp[len-1], "k", 1) == 0 || strncmp(&cp[len-1], "K", 1) == 0 ){
            len -= 1;
            unit *= 1024;
        }
        else if ( strncmp(&cp[len-1], "m", 1) == 0 || strncmp(&cp[len-1], "M", 1) == 0 ){
            len -= 1;
            unit *= 1024*1024;
        }
        strncpy(lstr, cp, len);
        sscanf(lstr, "%d", &val);
        if ( val <= 0 ) ompc_fatal("bad OMPC_STACK_SIZE(<= 0)");
        maxstack = val*unit;
        if ( maxstack < DEF_STACK_SIZE ){
            maxstack = 0;       /* default */
            printf("Stack size is not change, because it is less than the default(=1MB).\n");
        }
    }

    ompc_task_end = 0;

    /* hash table initialize */
    bzero(ompc_proc_htable, sizeof(ompc_proc_htable));

    /* allocate proc structure */
    ompc_procs =
        (struct ompc_proc *)malloc(sizeof(struct ompc_proc)*ompc_max_threads);
    if(ompc_procs == NULL) ompc_fatal("Cannot allocate proc table.");
    bzero(ompc_procs,sizeof(struct ompc_proc)*ompc_max_threads);

    /* init system lock */
    ompc_init_lock(&ompc_proc_lock_obj);
    ompc_init_lock(&ompc_thread_lock_obj);
    ompc_critical_init ();     /* initialize critical lock */
    ompc_atomic_init_lock ();  /* initialize atomic lock */

        /* add (and init proc table) this as master thread */
    cproc = ompc_new_proc();
    cproc->is_used = TRUE;  /* this is always used */
    ompc_master_proc_id = _OMPC_PROC_SELF;

    if(ompc_debug_flag)
        fprintf(stderr, "Creating %d slave thread ...\n", ompc_max_threads-1);

    for( t = 1; t < ompc_max_threads; t++ ){
        if(ompc_debug_flag) fprintf(stderr, "Creating slave %d  ...\n", t);

        r = ABT_xstream_create(ABT_SCHED_NULL, &xstreams[t]);
        ABT_thread_create_on_xstream(xstreams[t], ompc_xstream_setup, NULL, ABT_THREAD_ATTR_NULL, NULL);

        if ( r ){
            extern int errno;
            fprintf(stderr, "thread create fails at id %d:%d errno=%d\n", t, r, errno);
            perror("thread creation");
            exit(1);
        }
    }

    OMPC_WAIT((volatile int)ompc_proc_counter != (volatile int)ompc_max_threads);

    /* setup master root thread */
    tp = ompc_alloc_thread(cproc);
    tp->num             = 0;    /* team master */
    tp->in_parallel     = 0;
    tp->parent          = NULL;
    cproc->thr          = tp;

    if(ompc_debug_flag) fprintf(stderr, "init end(Master)\n");
}


/* finalize */
void
ompc_finalize()
{
#ifdef USE_LOG
    if(ompc_log_flag){
        tlog_finalize();
    }
#endif /* USE_LOG */
}

void
ompc_fatal(char * msg)
{
    fprintf(stderr, "OMPC FATAL: %s\n", msg);
    exit(1);
}

int
ompc_is_master_proc()
{
    return ompc_master_proc_id == _OMPC_PROC_SELF;
}

/* setup new ompc_proc: master is always at first proc table */
static struct ompc_proc *
ompc_new_proc()
{
    struct ompc_proc  * p, ** pp;
    ompc_proc_t  id = _OMPC_PROC_SELF;

    OMPC_PROC_LOCK();
    p = &ompc_procs[ompc_proc_counter];
    ompc_proc_counter++;

    p->pid = id;
    p->thr = NULL;
    /* add this proc table to hash */
    pp = &ompc_proc_htable[PROC_HASH_IDX(id)];
    p->link = *pp;
    *pp = p;

    OMPC_PROC_UNLOCK();
    return p;
}

/*static*/ struct ompc_thread *
ompc_current_thread()
{
    ompc_proc_t id;
    struct ompc_proc *p;
    struct ompc_thread *tp;

    id = _OMPC_PROC_SELF;

    for( p = ompc_proc_htable[PROC_HASH_IDX(id)]; p != NULL; p = p->link ){
        if(p->pid == id){
            if((tp = p->thr) == NULL)
                ompc_fatal("unkonwn thread is running");
            return tp;
        }
    }
    ompc_fatal("unknown proc is running");
    return NULL;
}

static struct ompc_proc *
ompc_current_proc()
{
    ompc_proc_t id;
    struct ompc_proc *p;

    id = _OMPC_PROC_SELF;
    for(p = ompc_proc_htable[PROC_HASH_IDX(id)]; p != NULL; p = p->link){
        if ( p->pid == id )
            return p;
    }
    fprintf(stderr, "pid=%d\n", (unsigned int)id);
    ompc_fatal("unknown proc is running");
    return NULL;
}

/* get thread from free list */
static struct ompc_proc *
ompc_get_proc()
{
    struct ompc_proc *p;
    int i;
    static int last_used = 0;

    OMPC_PROC_LOCK();
    if(++last_used >= ompc_max_threads) last_used = 0;
    p = &ompc_procs[last_used];
    p->is_used = TRUE;
    OMPC_PROC_UNLOCK();
    __sync_fetch_and_add(&p->thread_count, 1);

    return p;
}

static void
ompc_free_proc(struct ompc_proc *p)
{
    OMPC_PROC_LOCK();
    p->is_used = FALSE;
    OMPC_PROC_UNLOCK();
    __sync_fetch_and_sub(&p->thread_count, 1);
}

/* allocate/get thread entry */
static struct ompc_thread *
ompc_alloc_thread(struct ompc_proc *proc)
{
    struct ompc_thread *p;

    if ((p = proc->free_thr) != NULL)
        proc->free_thr = p->freelist;
    else {
        p = (struct ompc_thread *)malloc(sizeof(struct ompc_thread));
        if (p == NULL)
            ompc_fatal("ompc_alloc_thread: malloc failed");
    }

    return p;
}

static void
ompc_free_thread(struct ompc_proc *proc,struct ompc_thread *p)
{
    p->freelist = proc->free_thr;
    proc->free_thr = p;
}

static void ompc_xstream_setup()
{
#ifdef USE_LOG
    if(ompc_log_flag) {
      tlog_slave_init ();
    }
#endif /* USE_LOG */

    ompc_new_proc();
}

static void ompc_thread_wrapper_func(void *args)
{
    struct ompc_proc *cproc = ompc_current_proc();
    struct ompc_thread *tp = cproc->thr->parent;
    int i = cproc->thr->num;

# ifdef USE_LOG
    if(ompc_log_flag) tlog_parallel_IN(i);
# endif /* USE_LOG */

    if ( tp->nargs < 0) {
        /* call C function */
        if ( tp->args != NULL )
            (*tp->func)(tp->args, cproc->thr);
        else
            (*tp->func)(cproc->thr);
    } else {
        /* call Fortran function */
        ompc_call_fsub(tp);
    }

# ifdef USE_LOG
    if(ompc_log_flag) tlog_parallel_OUT(i);
# endif /* USE_LOG */

    /* on return, clean up */
    struct ompc_thread *me = cproc->thr;
    cproc->thr = NULL;
    ompc_free_thread(cproc,me);    /* free thread & put me to freelist */
    ompc_free_proc(cproc);
    ompc_thread_barrier2(i,tp);
}

/* called from compiled code. */
void
ompc_do_parallel_main (int nargs, int cond, int nthds,
    cfunc f, void *args)
{
    struct ompc_proc *cproc, *proclist, *p;
    struct ompc_thread *cthd, *tp;
    int i, n_thds, max_thds, in_parallel;

    cproc = ompc_current_proc();
    cthd  = cproc->thr;

    if (cond == 0) { /* serialized by parallel if(false) */
        max_thds = 1;
        in_parallel = cthd->in_parallel;
    } else {
        max_thds = (nthds < ompc_num_threads) ? (nthds) : (ompc_num_threads);
        in_parallel = 1;
    }

    proclist = NULL;
    for( n_thds = 1; n_thds < max_thds; n_thds ++ ){
        if ((p = ompc_get_proc()) == NULL){
          break;
        }
        p->next = proclist;
        proclist = p;
    }

    /* initialize parent thread */
    cthd->num_thds = n_thds;
    cthd->nargs = nargs;
    cthd->args = args;
    cthd->func = f;

    /* initialize barrier structure */
    cthd->out_count = 0;
    cthd->in_count = 0;
    for( i = 0; i < n_thds; i++ ){
        cthd->barrier_flags[i]._v = cthd->barrier_sense;
        cthd->in_flags[i]._v = 0;
    }

    /* assign thread to proc */
    for( i = 1; i < n_thds; i++ ){
        p = proclist;
        proclist = proclist->next;
        tp = ompc_alloc_thread(p);
        tp->parent = cthd;
        tp->num = i;                        /* set thread_num */
        tp->in_parallel = in_parallel;
        p->thr = tp;                        /* start it ! */

        ABT_thread_create_on_xstream((ABT_xstream)(p->pid), ompc_thread_wrapper_func,
            NULL, ABT_THREAD_ATTR_NULL, NULL);

        MBAR();
    }

    if (n_thds > 1) {
        ABT_mutex_lock(ompc_proc_mutex);
        ABT_cond_broadcast(ompc_proc_cond);
        ABT_mutex_unlock(ompc_proc_mutex);
    }

    /* allocate master in this team */
    tp = ompc_alloc_thread(cproc);
    tp->parent = cthd;
    tp->num = 0;  /* this is master */
    tp->in_parallel = in_parallel;
    tp->nargs = nargs;
    tp->args = args;
    cproc->thr = tp;

#ifdef USE_LOG
    if(ompc_log_flag) tlog_parallel_IN(0);
#endif /* USE_LOG */
    /* execute on master */
    if ( nargs < 0) {
        /* call C function */
        if ( args == NULL )
            (*f)(tp);
        else
            (*f)(args, tp);
    } else {
        /* call Fortran function */
        ompc_call_fsub(cthd);
    }
#ifdef USE_LOG
    if(ompc_log_flag) tlog_parallel_OUT(0);
#endif /* USE_LOG */

    /* clean up this thread */
    ompc_free_thread(cproc,tp);
    ompc_thread_barrier2(0, cthd);
    cproc->thr = cthd;
}


void
ompc_do_parallel(cfunc f, void *args)
{
    ompc_do_parallel_main (-1, 1, 10, f, args);
}

void
ompc_do_parallel_if (int cond, cfunc f, void *args)
{
    ompc_do_parallel_main (-1, cond, 10, f, args);
}


/* 
 * Barrier 
 */
void
ompc_thread_barrier(int id, struct ompc_thread *tpp)
{
    int sen0,n;

    if(tpp == NULL) return; /* not in parallel */
#ifdef USE_LOG
    if(ompc_log_flag) tlog_barrier_IN(id);
#endif // USE_LOG

#if 1  // USE_ARGOBOTS
    sen0 = ~tpp->barrier_sense;
    n = tpp->num_thds;

    if (id == 0) {
        for (int i = 1; i < n; i++) {
            if ((volatile int)tpp->barrier_flags[i]._v != sen0) {
                ABT_mutex_lock(ompc_mainwait_mutex);
                while ((volatile int)tpp->barrier_flags[i]._v != sen0) {
                    ABT_cond_wait(ompc_mainwait_cond, ompc_mainwait_mutex);
                }
                ABT_mutex_unlock(ompc_mainwait_mutex);
            }
        }

        ABT_mutex_lock(ompc_proc_mutex);
        tpp->barrier_sense = sen0;
        ABT_cond_broadcast(ompc_proc_cond);
        ABT_mutex_unlock(ompc_proc_mutex);
    } else {
        ABT_mutex_lock(ompc_mainwait_mutex);
        tpp->barrier_flags[id]._v = sen0;
        ABT_cond_signal(ompc_mainwait_cond);
        ABT_mutex_unlock(ompc_mainwait_mutex);

        if ((volatile int)tpp->barrier_sense != sen0) {
            ABT_mutex_lock(ompc_proc_mutex);
            while ((volatile int)tpp->barrier_sense != sen0) {
                ABT_cond_wait(ompc_proc_cond, ompc_proc_mutex);
            }
            ABT_mutex_unlock(ompc_proc_mutex);
        }
    }
#else
    sen0 = tpp->barrier_sense ^ 1;
    n = tpp->num_thds;
    if (id == 0){
        int j;
        for ( j = 1 ; j < n ; j++ )
          OMPC_WAIT((volatile int)tpp->barrier_flags[j]._v != sen0);
        tpp->barrier_sense = sen0;
        MBAR();
    } else {
        tpp->barrier_flags[id]._v = sen0;
        MBAR();
        OMPC_WAIT ((volatile int)tpp->barrier_sense != sen0);
    }
#endif  // USE_ARGOBOTS

#ifdef USE_LOG
    if(ompc_log_flag) tlog_barrier_OUT(id);
#endif // USE_LOG
}


void
ompc_thread_barrier2(int id, struct ompc_thread *tpp)
{
    int sen0,n;

    if(tpp == NULL) return; /* not in parallel */
#ifdef USE_LOG
    if(ompc_log_flag) tlog_barrier_IN(id);
#endif // USE_LOG
    sen0 = tpp->barrier_sense ^ 1;
    n = tpp->num_thds;

#if 1  // USE_ARGOBOTS
    if (id == 0) {
        for (int i = 1; i < n; i++) {
            if ((volatile int)tpp->barrier_flags[i]._v != sen0) {
                ABT_mutex_lock(ompc_mainwait_mutex);
                while ((volatile int)tpp->barrier_flags[i]._v != sen0) {
                    ABT_cond_wait(ompc_mainwait_cond, ompc_mainwait_mutex);
                }
                ABT_mutex_unlock(ompc_mainwait_mutex);
            }
        }
        tpp->barrier_sense = sen0;
        MBAR();
    } else {
        ABT_mutex_lock(ompc_mainwait_mutex);
        tpp->barrier_flags[id]._v = sen0;
        ABT_cond_signal(ompc_mainwait_cond);
        ABT_mutex_unlock(ompc_mainwait_mutex);
    }
#else
    if (id == 0) {
        int j;
        for ( j = 1 ; j < n ; j++ ) {
            OMPC_WAIT((volatile int)tpp->barrier_flags[j]._v != sen0);
        }
        tpp->barrier_sense = sen0;
        MBAR();
    } else {
        tpp->barrier_flags[id]._v = sen0;
    }
#endif  // USE_ARGOBOTS

#ifdef USE_LOG
    if(ompc_log_flag) tlog_barrier_OUT(id);
#endif // USE_LOG
}


void
ompc_current_thread_barrier()
{
    int id;
    struct ompc_thread *tp = ompc_current_thread();
    struct ompc_thread *tpp = tp->parent;

    if(tpp == NULL)
        return;

    if(ompc_get_num_threads(tp) == 1) {
        id = 0;
    } else {
        id = tp->num;
    }

    ompc_thread_barrier(id, tpp);
}


void
ompc_terminate (int exitcode)
{
    for (int i = 1; i < ompc_proc_counter; i++) {
        ABT_xstream_free((ABT_xstream *)&(ompc_procs[i].pid));
    }
    ABT_finalize();

    exit (exitcode);
}


int
ompc_in_parallel (struct ompc_thread *tp)
{
    return tp->in_parallel;
}


int
ompc_get_num_threads (struct ompc_thread *tp)
{
    if((tp = tp->parent) == NULL)
        return 1;
    else 
        return tp->num_thds;
}


void
ompc_set_num_threads(int n)
{
    extern void omp_set_num_threads();
    omp_set_num_threads(n);
}


int
ompc_get_thread_num()
{
    extern int omp_get_thread_num();
    return omp_get_thread_num();
}


int
ompc_get_max_threads()
{
    return ompc_max_threads;
}

ompc_proc_t
ompc_xstream_self()
{
    ABT_xstream xstream;
    ABT_xstream_self(&xstream);
    return xstream;
}

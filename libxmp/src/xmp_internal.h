#ifndef _XCALABLEMP_INTERNAL
#define _XCALABLEMP_INTERNAL

// --------------- including headers  --------------------------------
#include <assert.h>
#include <stddef.h>
#include <stdbool.h>
#include "mpi.h"

// --------------- structures ----------------------------------------
// nodes descriptor
typedef struct _XCALABLEMP_nodes_info_type {
  int size;

  // enable when is_member is true
  int rank;
  // -----------------------------
} _XCALABLEMP_nodes_info_t;

typedef struct _XCALABLEMP_nodes_type {
  _Bool is_member;
  int dim;
  int comm_size;

  // enable when is_member is true
  int comm_rank;
  MPI_Comm *comm;
  // -----------------------------

  _XCALABLEMP_nodes_info_t info[1];
} _XCALABLEMP_nodes_t;

// template desciptor
typedef struct _XCALABLEMP_template_info_type {
  long long ser_lower;
  long long ser_upper;
  unsigned long long ser_size;
} _XCALABLEMP_template_info_t;

typedef struct _XCALABLEMP_template_chunk_type {
  // enable when dist_manner is not _XCALABLEMP_N_DIST_DUPLICATION
  int onto_nodes_index;
  _XCALABLEMP_nodes_info_t *onto_nodes_info;

  long long par_lower;
  long long par_upper;
  // -------------------------------------------------------------

  int par_stride;
  unsigned long long par_chunk_width;
  int dist_manner;
  _Bool is_regular_chunk;
} _XCALABLEMP_template_chunk_t;

typedef struct _XCALABLEMP_template_type {
  _Bool is_owner;
  _Bool is_fixed;
  int   dim;

  // enable when template is distributed
  _XCALABLEMP_nodes_t *onto_nodes;
  _XCALABLEMP_template_chunk_t *chunk;
  // -----------------------------------

  _XCALABLEMP_template_info_t info[1];
} _XCALABLEMP_template_t;

typedef struct _XCALABLEMP_array_info_type {
  int ser_lower;
  int ser_upper;
  int ser_size;

  // FIXME not support BLOCK_CYCLIC, GEN_BLOCK yet
  // enable when is_allocated is true
  int par_lower;
  int par_upper;
  int par_stride;
  int par_size;

  int dist_manner;
  _Bool is_regular_chunk;

  int local_lower;
  int local_upper;
  int local_stride;
  int alloc_size;

  // enable when distribution is BLOCK/CYCLIC
  int *temp0;
  // ----------------------------------------

  unsigned long long dim_acc;
  unsigned long long dim_elmts;

  MPI_Comm *shadow_comm;
  int shadow_comm_size;
  int shadow_comm_rank;
  // --------------------------------

  long long align_subscript;

  // FIXME needs refactoring
  int shadow_type;
  int shadow_size_lo;
  int shadow_size_hi;

  int align_template_index;
  // enable align_template_index is not _XCALABLEMP_N_NO_ALIGNED_TEMPLATE
  _XCALABLEMP_template_info_t *align_template_info;
  _XCALABLEMP_template_chunk_t *align_template_chunk;
  // --------------------------------------------------------------------
} _XCALABLEMP_array_info_t;

typedef struct _XCALABLEMP_array_type {
  _Bool is_allocated;
  int dim;
  int type;
  size_t type_size;

  // enable when is_allocated is true
  void **addr;
  unsigned long long total_elmts;
  // --------------------------------

  // enable when is_member is true
  MPI_Comm *comm;
  int comm_size;
  int comm_rank;
  // -----------------------------

  _XCALABLEMP_template_t *align_template;
  _XCALABLEMP_array_info_t info[1];
} _XCALABLEMP_array_t;

// --------------- variables -----------------------------------------
// xmp_world.c
extern int _XCALABLEMP_world_size;
extern int _XCALABLEMP_world_rank;
extern void *_XCALABLEMP_world_nodes;

// --------------- functions -----------------------------------------
// xmp_array_section.c
extern void _XCALABLEMP_normalize_array_section(int *lower, int *upper, int *stride);
extern void _XCALABLEMP_pack_array_BASIC(void *buffer, void *src, int array_type,
                                         int array_dim, int *l, int *u, int *s, unsigned long long *d);
extern void _XCALABLEMP_pack_array_GENERAL(void *buffer, void *src, size_t array_type_size,
                                           int array_dim, int *l, int *u, int *s, unsigned long long *d);
extern void _XCALABLEMP_unpack_array_BASIC(void *dst, void *buffer, int array_type,
                                           int array_dim, int *l, int *u, int *s, unsigned long long *d);
extern void _XCALABLEMP_unpack_array_GENERAL(void *dst, void *buffer, size_t array_type_size,
                                             int array_dim, int *l, int *u, int *s, unsigned long long *d);

// xmp_barrier.c
extern void _XCALABLEMP_barrier_EXEC(void);

// xmp_nodes.c
extern void _XCALABLEMP_validate_nodes_ref(int *lower, int *upper, int *stride, int size);
extern void _XCALABLEMP_finalize_nodes(_XCALABLEMP_nodes_t *nodes);
extern _XCALABLEMP_nodes_t *_XCALABLEMP_create_nodes_by_comm(MPI_Comm *comm);

// xmp_nodes_stack.c
extern void _XCALABLEMP_push_nodes(_XCALABLEMP_nodes_t *nodes);
extern void _XCALABLEMP_pop_nodes(void);
extern void _XCALABLEMP_pop_n_free_nodes(void);
extern void _XCALABLEMP_pop_n_free_nodes_wo_finalize_comm(void);
extern _XCALABLEMP_nodes_t *_XCALABLEMP_get_execution_nodes(void);
extern int _XCALABLEMP_get_execution_nodes_rank(void);
extern void _XCALABLEMP_push_comm(MPI_Comm *comm);
extern void _XCALABLEMP_finalize_comm(MPI_Comm *comm);

// xmp_util.c
extern void *_XCALABLEMP_alloc(size_t size);
extern void _XCALABLEMP_free(void *p);
extern void _XCALABLEMP_fatal(char *msg);

// xmp_world.c
extern void _XCALABLEMP_init_world(int *argc, char ***argv);
extern void _XCALABLEMP_barrier_WORLD(void);
extern int _XCALABLEMP_finalize_world(int ret);

#endif // _XCALABLEMP_INTERNAL

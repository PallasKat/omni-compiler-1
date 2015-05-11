#include "xmpf_internal.h"

/*****************************************\
  initialization
\*****************************************/

static int _this_image, _num_images;

void _XMPF_set_this_image()
{
  int size, rank;

  if (MPI_Comm_size(MPI_COMM_WORLD, &size) != 0)
    _XMPF_coarrayFatal("INTERNAL ERROR: illegal node size of COMM_WORLD");
  if (MPI_Comm_rank(MPI_COMM_WORLD, &rank) != 0)
    _XMPF_coarrayFatal("INTERNAL ERROR: illegal node rank of mine");

  _num_images = size;
  _this_image = rank + 1;
}


/*****************************************\
  transformation functions
\*****************************************/

/*  MPI_Comm_size() of the current communicator
 */
int num_images_(void)
{
  _XMPF_checkIfInTask("NUM_IMAGES");

  return _num_images;
}


/*  (MPI_Comm_rank() + 1) in the current communicator
 */
int this_image_(void)
{
  _XMPF_checkIfInTask("THIS_IMAGE");

  return _this_image;
}


/*****************************************\
  sync all
\*****************************************/

static unsigned int _count_syncall = 0;

void xmpf_sync_all_nostat_(void)
{
  _XMPF_checkIfInTask("syncall nostat");

  _count_syncall += 1;

  int status = 0;
  xmp_sync_all(&status);

  _XMPF_coarrayDebugPrint("SYNCALL out (_count_syncall=%d, status=%d)\n",
                          _count_syncall, status);
}

/* entry for automatic syncall at the end of procedures
 */
void xmpf_sync_all_auto_(void)
{
  _XMPF_checkIfInTask("syncall nostat");

  _count_syncall += 1;

  int status = 0;
  xmp_sync_all(&status);

  _XMPF_coarrayDebugPrint("SYNCALL_AUTO out (_count_syncall=%d, status=%d)\n",
                          _count_syncall, status);
}

void xmpf_sync_all_stat_(int *stat, char *msg, int *msglen)
{
  _XMPF_checkIfInTask("syncall with stat");

  static BOOL firstCall = TRUE;
  if (firstCall) {
    firstCall = FALSE;
    fprintf(stderr, "not supported yet: "
            "stat= specifier in SYNCALL statement\n");
    fprintf(stderr, "  -- ignored.\n");
  }

  int status;
  xmp_sync_all(&status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC ALL failed (xmpf_sync_all_stat_)");
}


/*****************************************\
  sync memory
\*****************************************/

void xmpf_sync_memory_nostat_(void)
{
  _XMPF_checkIfInTask("syncmemory nostat");

  int status;
  xmp_sync_memory(&status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC MEMORY failed (xmpf_sync_memory_nostat_)");
}

void xmpf_sync_memory_stat_(int *stat, char *msg, int *msglen)
{
  _XMPF_checkIfInTask("syncmemory with stat");

  static BOOL firstCall = TRUE;
  if (firstCall) {
    firstCall = FALSE;
    fprintf(stderr, "not supported yet: "
            "stat= specifier in SYNC MEMORY statement\n");
    fprintf(stderr, "  -- ignored.\n");
  }

  int status;
  xmp_sync_memory(&status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC MEMORY failed (xmpf_sync_memory_stat_)");
}


/*****************************************\
  sync images
\*****************************************/

void xmpf_sync_image_nostat_(int *image)
{
  int status;
  xmp_sync_image(*image, &status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC IMAGES failed (xmpf_sync_image_nostat_)");
}

void xmpf_sync_image_stat_(int *image, int *stat, char *msg, int *msglen)
{
  static BOOL firstCall = TRUE;
  if (firstCall) {
    firstCall = FALSE;
    fprintf(stderr, "not supported yet: "
            "stat= specifier in SYNC IMAGES (<image>) statement\n");
    fprintf(stderr, "  -- ignored.\n");
  }

  _XMPF_checkIfInTask("syncimage with stat");

  int status;
  xmp_sync_image(*image, &status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC IMAGES failed (xmpf_sync_image_stat_)");
}


void xmpf_sync_images_nostat_(int *images, int *size)
{
  int status;
  xmp_sync_images(*size, images, &status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC IMAGES failed (xmpf_sync_images_nostat_)");
}

void xmpf_sync_images_stat_(int *images, int *size, int *stat,
                            char *msg, int *msglen)
{
  static BOOL firstCall = TRUE;
  if (firstCall) {
    firstCall = FALSE;
    fprintf(stderr, "not supported yet: "
            "stat= specifier in SYNC IMAGES (<images>) statement\n");
    fprintf(stderr, "  -- ignored.\n");
  }

  _XMPF_checkIfInTask("syncimage with stat");

  int status;
  xmp_sync_images(*size, images, &status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC IMAGES failed (xmpf_sync_images_stat_)");
}


void xmpf_sync_allimages_nostat_(void)
{
  int status;
  xmp_sync_images_all(&status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC IMAGES failed (xmpf_sync_allimages_nostat_)");
}

void xmpf_sync_allimages_stat_(int *stat, char *msg, int *msglen)
{
  static BOOL firstCall = TRUE;
  if (firstCall) {
    firstCall = FALSE;
    fprintf(stderr, "not supported yet: "
            "stat= specifier in SYNC IMAGES (*) statement\n");
    fprintf(stderr, "  -- ignored.\n");
  }

  _XMPF_checkIfInTask("syncimage with stat");

  int status;
  xmp_sync_images_all(&status);
  //  if (status != 0)
  //    _XMPF_coarrayFatal("SYNC IMAGES failed (xmpf_sync_allimages_stat_)");
}



/*****************************************\
  error message to reply to Fortran (temporary)
  (not used yet)
\*****************************************/

char *_XMPF_errmsg = NULL;

void xmpf_get_errmsg_(unsigned char *errmsg, int *msglen)
{
  int i, len;

  if (_XMPF_errmsg == NULL) {
    len = 0;
  } else {
    len = strlen(_XMPF_errmsg);
    if (len > *msglen)
      len = *msglen;
    memcpy(errmsg, _XMPF_errmsg, len);      // '\n' is not needed
  }

  for (i = len; i < *msglen; )
    errmsg[i++] = ' ';

  return;
}

  
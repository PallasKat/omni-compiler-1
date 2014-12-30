#define NMAX 8
#include <stdio.h>
#include <stdlib.h>

extern int chk_int(int ierr);

int n=NMAX;
int a[n],b[n];
#pragma xmp nodes p(2,2)
#pragma xmp template tx(0:n-1,0:n-1)
#pragma xmp distribute tx(block,block) onto p
#pragma xmp align a[i0] with tx(i0,*)
#pragma xmp align b[i0] with tx(i0,*)

int main(){

  int i0,i1,ierr;

#pragma xmp loop (i0) on tx(i0,*)
  for(i0=0;i0<n;i0++){
    a[i0]=i0+1;
  }

#pragma xmp loop (i0) on tx(i0,*)
  for(i0=0;i0<n;i0++){
    b[i0]=0;
  }

#pragma xmp gmove
  b[1:n-1]=a[1:n-1];

  ierr=0;
#pragma xmp loop (i0) on tx(i0,*)
  for(i0=1;i0<n;i0++){
    ierr=ierr+abs(b[i0]-i0-1);
  }

#pragma xmp reduction (MAX:ierr)
  chk_int(ierr);
}
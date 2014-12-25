#include <stdio.h>
#include <math.h>

extern int chk_int(int ierr);

int n=8;
double a[n][n][n],b[n][n][n];
int mx[2]={2,6};
int my[2]={2,6};
#pragma xmp nodes p(2,2,2)
#pragma xmp template tx(0:n-1,0:n-1,0:n-1)
#pragma xmp template ty(0:n-1,0:n-1,0:n-1)
#pragma xmp distribute tx(gblock(mx),gblock(mx),gblock(mx)) onto p
#pragma xmp distribute ty(gblock(my),gblock(my),gblock(my)) onto p
#pragma xmp align a[i][j][k] with tx(i,j,k)
#pragma xmp align b[i][j][k] with ty(i,j,k)

int main(){

  int i,j,k,ierr;
  double err ;

#pragma xmp loop (i,j,k) on tx(i,j,k)
  for(i=0;i<n;i++){
    for(j=0;j<n;j++){
      for(k=0;k<n;k++){
        a[i][j][k]=i+j+k+3;
      }
    }
  }

#pragma xmp loop (i,j,k) on ty(i,j,k)
  for(i=0;i<n;i++){
    for(j=0;j<n;j++){
      for(k=0;k<n;k++){
        b[i][j][k]=0;
      }
    }
  }

#pragma xmp gmove
  b[0:n][0:n][0:n]=a[0:n][0:n][0:n];

  err=0.0;
#pragma xmp loop (i,j,k) on ty(i,j,k)
  for(i=0;i<n;i++){
    for(j=0;j<n;j++){
      for(k=0;k<n;k++){
        err=err+fabs(b[i][j][k]-i-j-k-2);
      }
    }
  }

#pragma xmp reduction (MAX:err)
  ierr=err;
  chk_int(ierr);
}


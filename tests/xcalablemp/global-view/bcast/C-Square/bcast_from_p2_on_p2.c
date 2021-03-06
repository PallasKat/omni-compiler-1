#include <xmp.h>
#include <stdio.h>
#include <stdlib.h>
#pragma xmp nodes p[*][4]
static const int N=1000;
int i,j,k,aa[1000],a;
double bb[1000],b;
float cc[1000],c;
int procs,procs2,ans,result=0;

int main(void)
{
  procs = xmp_num_nodes();
  procs2 = procs/4;
  for(k=1;k<procs2+1;k++){
    for(j=1;j<5;j++){
      a = xmp_node_num();
      b = (double)a;
      c = (float)a;
      for(i=0;i<N;i++){
	aa[i] = a+i;
	bb[i] = (double)(a+i);
	cc[i] = (float)(a+i);
      }

#pragma xmp bcast (a) from p[k-1][j-1] on p[:][:]
#pragma xmp bcast (b) from p[k-1][j-1] on p[:][:]
#pragma xmp bcast (c) from p[k-1][j-1] on p[:][:]
#pragma xmp bcast (aa) from p[k-1][j-1] on p[:][:]
#pragma xmp bcast (bb) from p[k-1][j-1] on p[:][:]
#pragma xmp bcast (cc) from p[k-1][j-1] on p[:][:]
      
#pragma xmp barrier
      break;
      ans = (k-1)*4+j;
      if(a != ans)         result = -1;
      if(b != (double)ans) result = -1;
      if(c != (float)ans)  result = -1;
      for(i=0;i<N;i++){
	if(aa[i] != ans+i)           result = -1;
	if(bb[i] != (double)(ans+i)) result = -1;
	if(cc[i] != (float)(ans+i))  result = -1;
      }
    }
  }

#pragma xmp reduction(+:result)
#pragma xmp task on p[0][0]
  {
    if(result == 0){
      printf("PASS\n");
    }
    else{
      fprintf(stderr, "ERROR\n");
      exit(1);
    }
  }

  return 0;
}    

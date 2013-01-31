#include <stdio.h>
#include <xmp.h>
#define MAX_TAG 4
int *status;
#pragma xmp nodes p(*)

void init_tag(int tag[MAX_TAG]){
  int i;
  for(i=0;i<MAX_TAG;i++)    tag[i] = i;
}

void post_wait_local(){
#pragma xmp task on p(1)
  {
#pragma xmp post (p(1), -9)
#pragma xmp post (p(1), 8)
#pragma xmp post (p(1), 0)

#pragma xmp wait (p(1), 8)  // release  8 tag
#pragma xmp wait (p(1))     // release  0 tag
#pragma xmp wait            // release -1 tag
  }
  xmp_sync_all(status);
}

void post_wait_p2p(int tag[MAX_TAG]){
  init_tag(tag);
#pragma xmp task on p(1)
  {
#pragma xmp post (p(2), tag[3])
#pragma xmp post (p(2), tag[1])
#pragma xmp post (p(2), tag[2])
  }

#pragma xmp task on p(2)
  {
#pragma xmp wait                 // release tag[2]
#pragma xmp wait (p(1), tag[3])  // release tag[3]
#pragma xmp wait (p(1))          // release tag[1]
  }
  xmp_sync_all(status);
}

void post_wait_nodes(){
  int tag2 = 7;
#pragma xmp task on p(1)
{
  int target_node = 3;
#pragma xmp post (p(target_node), tag2)
 }

#pragma xmp task on p(2)
 {
   int target_node[100][3];
   target_node[3][2] = 3;
#pragma xmp post (p(target_node[3][2]), tag2)
 }

#pragma xmp task on p(3)
 {
#pragma xmp wait (p(1), tag2)
#pragma xmp wait (p(2), tag2)
 }

 xmp_sync_all(status);
}

int main(){
  post_wait_local();

  int tag[MAX_TAG];
  post_wait_p2p(tag);

  post_wait_nodes();

#pragma xmp task on p(1)
  {
    fprintf(stderr, "PASS\n");
  }
  return 0;
}

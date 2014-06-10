! testp135.f
! bcast�ؼ�ʸ�Υƥ��ȡ�from��̵����on��template-ref

      program main
      include 'xmp_lib.h'
      integer,parameter:: N=1000
!$xmp nodes p(*)
!$xmp template t(N,N)
!$xmp distribute t(*,cyclic(3)) onto p
      integer aa(N), a
      real*4 bb(N), b
      real*8 cc(N), c
      integer procs
      character(len=2) result

      procs = xmp_num_nodes()

      result = 'OK'
      j = 1
      a = xmp_node_num()
      b = real(a)
      c = dble(a)
      do i=1, N
         aa(i) = a+i-1
         bb(i) = real(a+i-1)
         cc(i) = dble(a+i-1)
      enddo
      
!$xmp bcast (a) on t(:,:)
!$xmp bcast (b) on t(:,:)
!$xmp bcast (c) on t(:,:)
!$xmp bcast (aa) on t(:,:)
!$xmp bcast (bb) on t(:,:)
!$xmp bcast (cc) on t(:,:)

      if(a .ne. j) result = 'NG'
      if(b .ne. real(j)) result = 'NG'
      if(c .ne. dble(j)) result = 'NG'
      do i=1, N
         if(aa(i) .ne. j+i-1) result = 'NG'
         if(bb(i) .ne. real(j+i-1)) result = 'NG'
         if(cc(i) .ne. dble(j+i-1)) result = 'NG'
      enddo
      
      print *, xmp_node_num(), 'testp135.f ', result

      end
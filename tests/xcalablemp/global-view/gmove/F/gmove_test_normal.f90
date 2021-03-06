module mod0_gmove_test_normal

  integer, parameter :: N = 64

!$xmp nodes p0(2,2)

!$xmp template t1(N,N,N)
!$xmp distribute t1(*,block,block) onto p0

  integer a(N,N,N)
!$xmp align a(i,j,k) with t1(i,j,k)
!$xmp shadow a(0,2:1,1:0)

!$xmp template t2(N,N,N)
!$xmp distribute t2(block,cyclic,*) onto p0

  integer b(N,N,N)
!$xmp align b(i,j,k) with t2(i,j,k)
!$xmp shadow b(0:1,0,0)

  integer x(N,N,N)

  integer s

contains

  subroutine init_a
!$xmp loop (i,j,k) on t1(i,j,k)
    do k = 1, N
       do j = 1, N
          do i = 1, N
             a(i,j,k) = 777
          end do
       end do
    end do
  end subroutine init_a

  subroutine init_b
!$xmp loop (i,j,k) on t2(i,j,k)
    do k = 1, N
       do j = 1, N
          do i = 1, N
             b(i,j,k) = i*10000 + j *100 + k
          end do
       end do
    end do
  end subroutine init_b

  subroutine init_x
    do k = 1, N
       do j = 1, N
          do i = 1, N
             x(i,j,k) = i*10000 + j *100 + k
          end do
       end do
    end do
  end subroutine init_x

  subroutine init_x0
    x = 0
  end subroutine init_x

end module mod0_gmove_test_normal


!--------------------------------------------------------
program gmove_test_normal

  use mod0_gmove_test_normal

#ifdef _MPI3
  call gmove_gs_gs
  call gmove_gs_ge
  call gmove_ge_ge

  call gmove_gs_ls
  call gmove_gs_le
  call gmove_ge_le

  call gmove_gs_s
  call gmove_ge_s

  call gmove_ls_gs
  call gmove_ls_ge
  call gmove_le_ge

  call gmove_s_ge

!$xmp task on p0(1,1) nocomm
  write(*,*) "PASS"
!$xmp end task
#else
!$xmp task on p0(1,1) nocomm
  write(*,*) "Skipped"
!$xmp end task
#endif

end program gmove_test_normal


!--------------------------------------------------------
! global section = global section
!--------------------------------------------------------
subroutine gmove_gs_gs

integer :: result = 0

  use mod0_gmove_test_normal

  call init_a
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(1:N/4, N/2+1:N, 5:N-1) = b(N/2+1:N:2, 1:N/2, 1:N-5)
#endif

!$xmp barrier

!$xmp loop (i,j,k) on t1(i,j,k)
  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (a(i,j,k) /= (N/2+1+(i-1)*2)*10000 + (j-N/2)*100 + (k-4)) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, a(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp reduction (+:result)

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_gs_gs"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_gs_gs


!--------------------------------------------------------
! global section = global element
!--------------------------------------------------------
subroutine gmove_gs_ge

integer :: result = 0

  use mod0_gmove_test_normal

  call init_a
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(1:N/4, N/2+1:N, 5:N-1) = b(3,4,5)
#endif

!$xmp barrier

!$xmp loop (i,j,k) on t1(i,j,k) reduction(+:result)
  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (a(i,j,k) /= 3*10000 + 4*100 + 5) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, a(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_gs_ge"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_gs_ge

!--------------------------------------------------------
! global element = global element
!--------------------------------------------------------
subroutine gmove_ge_ge

  use mod0_gmove_test_normal

  call init_a
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(7,8,9) = b(3,4,5)
#endif

!$xmp barrier

!$xmp task on t1(7,8,9) nocomm
  if (a(7,8,9) /= 3*10000 + 4*100 + 5) then
!     write(*,*) "(", xmp_node_num(), ")", 7, 8, 9, a(7,8,9)
     write(*,*) "ERROR in gmove_ge_ge"
     call exit(1)
  end if
!$xmp end task

end subroutine gmove_ge_ge


!--------------------------------------------------------
! global section = local section
!--------------------------------------------------------
subroutine gmove_gs_ls

integer :: result = 0

  use mod0_gmove_test_normal

  call init_a
  call init_x

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(1:N/4, N/2+1:N, 5:N-1) = x(N/2+1:N:2, 1:N/2, 1:N-5)
#endif

!$xmp barrier

!$xmp loop (i,j,k) on t1(i,j,k) reduction(+:result)
  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (a(i,j,k) /= (N/2+1+(i-1)*2)*10000 + (j-N/2)*100 + (k-4)) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, a(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_gs_ls"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_gs_ls


!--------------------------------------------------------
! global section = local element
!--------------------------------------------------------
subroutine gmove_gs_le

integer :: result = 0

  use mod0_gmove_test_normal

  call init_a
  call init_x

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(1:N/4, N/2+1:N, 5:N-1) = x(3,4,5)
#endif

!$xmp barrier

!$xmp loop (i,j,k) on t1(i,j,k) reduction(+:result)
  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (a(i,j,k) /= 3*10000 + 4*100 + 5) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, a(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_gs_le"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_gs_le

!--------------------------------------------------------
! global element = local element
!--------------------------------------------------------
subroutine gmove_ge_le

  use mod0_gmove_test_normal

  call init_a
  call init_x

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(7,8,9) = x(3,4,5)
#endif

!$xmp barrier

!$xmp task on t1(7,8,9) nocomm
  if (a(7,8,9) /= 3*10000 + 4*100 + 5) then
!     write(*,*) "(", xmp_node_num(), ")", 7, 8, 9, a(7,8,9)
     write(*,*) "ERROR in gmove_ge_le"
     call exit(1)
  end if
!$xmp end task

end subroutine gmove_ge_le


!--------------------------------------------------------
! global section = scalar
!--------------------------------------------------------
subroutine gmove_gs_s

integer :: result = 0

  use mod0_gmove_test_normal

  call init_a
  s = 111

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(1:N/4, N/2+1:N, 5:N-1) = s
#endif

!$xmp barrier

!$xmp loop (i,j,k) on t1(i,j,k) reduction(+:result)
  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (a(i,j,k) /= s) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, a(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_gs_s"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_gs_s

!--------------------------------------------------------
! global element = scalar
!--------------------------------------------------------
subroutine gmove_ge_s

  use mod0_gmove_test_normal

  call init_a
  s = 111

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  a(7,8,9) = s
#endif

!$xmp barrier

!$xmp task on t1(7,8,9) nocomm
  if (a(7,8,9) /= s) then
!     write(*,*) "(", xmp_node_num(), ")", 7, 8, 9, a(7,8,9)
     write(*,*) "ERROR in gmove_ge_s"
     call exit(1)
  end if
!$xmp end task

end subroutine gmove_ge_s


!--------------------------------------------------------
! local section = global section
!--------------------------------------------------------
subroutine gmove_ls_gs

integer :: result = 0

  use mod0_gmove_test_normal

  call init_x0
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  x(1:N/4, N/2+1:N, 5:N-1) = b(N/2+1:N:2, 1:N/2, 1:N-5)
#endif

!$xmp barrier

  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (x(i,j,k) /= (N/2+1+(i-1)*2)*10000 + (j-N/2)*100 + (k-4)) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, x(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp reduction (+:result)

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_ls_gs"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_ls_gs


!--------------------------------------------------------
! local section = global element
!--------------------------------------------------------
subroutine gmove_ls_ge

integer :: result = 0

  use mod0_gmove_test_normal

  call init_x0
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  x(1:N/4, N/2+1:N, 5:N-1) = b(3,4,5)
#endif

!$xmp barrier

  do k = 5, N-1
     do j = N/2+1, N
        do i = 1, N/4
           if (x(i,j,k) /= 3*10000 + 4*100 + 5) then
!              write(*,*) "(", xmp_node_num(), ")", i, j, k, x(i,j,k)
              result = 1
           end if
        end do
     end do
  end do

!$xmp reduction (+:result)

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "ERROR in gmove_ls_ge"
     call exit(1)
  endif
!$xmp end task

end subroutine gmove_ls_ge

!--------------------------------------------------------
! local element = global element
!--------------------------------------------------------
subroutine gmove_le_ge

  integer :: result = 0

  use mod0_gmove_test_normal

  call init_x0
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  x(7,8,9) = b(3,4,5)
#endif

!$xmp barrier

  if (x(7,8,9) /= 3*10000 + 4*100 + 5) then
     result = 1
  end if

!$xmp reduction (+:result)

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
!     write(*,*) "(", xmp_node_num(), ")", 7, 8, 9, x(7,8,9)
     write(*,*) "ERROR in gmove_le_ge"
     call exit(1)
  end if
!$xmp end task

end subroutine gmove_le_ge


!--------------------------------------------------------
! scalar = global element
!--------------------------------------------------------
subroutine gmove_s_ge

  integer :: result = 0

  use mod0_gmove_test_normal

  s = 0
  call init_b

!$xmp barrier

#ifdef _MPI3
!$xmp gmove
  s = b(3,4,5)
#endif

!$xmp barrier

!$xmp barrier

  if (s /= 3*10000 + 4*100 + 5) then
     result = 1
  end if

!$xmp reduction (+:result)

!$xmp task on p0(1,1) nocomm
  if (result /= 0) then
     write(*,*) "(", xmp_node_num(), ")", s
     write(*,*) "ERROR in gmove_s_ge"
     call exit(1)
  end if
!$xmp end task

end subroutine gmove_s_ge

program tgmove
integer i
integer,parameter :: n=8
integer a(n,n,n,n,n),b(n,n,n,n,n)
integer xmp_node_num
!$xmp nodes p(2,2,2,2,2)
!$xmp template tx(n,n,n,n,n)
!$xmp template ty(n,n,n,n,n)
!$xmp distribute tx(block,block,block,block,block) onto p
!$xmp distribute ty(cyclic,cyclic,cyclic,cyclic,cyclic) onto p
!$xmp align a(i0,i1,i2,i3,i4) with tx(i0,i1,i2,i3,i4)
!$xmp align b(i0,i1,i2,i3,i4) with ty(i0,i1,i2,i3,i4)

!$xmp loop (i0,i1,i2,i3,i4) on tx(i0,i1,i2,i3,i4)
do i4=1,n
  do i3=1,n
    do i2=1,n
      do i1=1,n
        do i0=1,n
          a(i0,i1,i2,i3,i4)=i0+i1+i2+i3+i4
        end do
      end do
    end do
  end do
end do

!$xmp loop (i0,i1,i2,i3,i4) on ty(i0,i1,i2,i3,i4)
do i4=1,n
  do i3=1,n
    do i2=1,n
      do i1=1,n
        do i0=1,n
          b(i0,i1,i2,i3,i4)=0
        end do
      end do
    end do
  end do
end do

!$xmp gmove
b(2:5,2:5,2:5,2:5,2:5)=a(5:8,5:8,5:8,5:8,5:8)

ierr=0
!$xmp loop (i0,i1,i2,i3,i4) on ty(i0,i1,i2,i3,i4)
do i4=2,5
  do i3=2,5
    do i2=2,5
      do i1=2,5
        do i0=2,5
          ierr=ierr+abs(b(i0,i1,i2,i3,i4)-(i0+3)-(i1+3)-(i2+3)-(i3+3)-(i4+3))
!          print *, 'i0,i1,i2,i3,i4=',i0,i1,i2,i3,i4,'b=',b(i0,i1,i2,i3,i4)
        end do
      end do
    end do
  end do
end do

!$xmp reduction (max:ierr)
call chk_int(ierr)

stop
end program tgmove

      program main
      include 'xmp_lib.h'
      integer,parameter:: N=1000
      integer random_array(N), ans_val, val, result
!$xmp nodes p(*)
!$xmp template t(N)
!$xmp distribute t(block) onto p

      result = 0
      do k=114, 10000, 17
         random_array(1) = k
         do i=2, N
            random_array(i) =  mod(random_array(i-1)**2, 100000000)
            random_array(i) =  mod((random_array(i)-mod(random_array(i),100))/100, 10000)
         enddo

         ans_val = 0
         do i=1, N
            ans_val = max(ans_val, random_array(i))
         enddo

         val = 0
!$xmp loop on t(i)
         do i=1, N
            val = max(val, random_array(i))
         enddo
!$xmp reduction(max: val)
         if(val .ne. ans_val) then
            print *, val, '!=', ans_val
            result = -1 ! NG
         endif
      enddo

!$xmp reduction(+:result)
!$xmp task on p(1)
      if (result .eq. 0) then
         write(*,*) "PASS"
      else
         write(*,*) "ERROR"
         call exit(1)
      end if
!$xmp end task
      end

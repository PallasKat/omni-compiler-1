      module m1
        integer :: i
      end module m1

      subroutine sub(i)
        use m1
        intent(in) :: i
      end subroutine sub

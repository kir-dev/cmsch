import { Center, Spinner, useColorModeValue } from '@chakra-ui/react'
import { ReactNode, useEffect, useState } from 'react'

type LoadingProps = {
  timeout?: number
  children?: ReactNode
}

/**
 * Displays the given loading component or a Spinner after the given timeout or one second.
 * @param timeout Number, Optional - The timeout in millisec before the component appears. Default value is 0
 * @param children ReactNode, Optional - The component to be displayed instead of a brand colored spinner.
 * @constructor
 */
export const Loading = ({ timeout = 0, children }: LoadingProps) => {
  const [show, setShow] = useState<boolean>(false)
  const color = useColorModeValue('brand.500', 'brand.600')
  useEffect(() => {
    setTimeout(() => {
      setShow(true)
    }, timeout)
  }, [setTimeout, timeout])
  if (!show) return null
  return children ? (
    <>{children}</>
  ) : (
    <Center>
      <Spinner color={color} size="xl" thickness="0.3rem" my={10} />
    </Center>
  )
}

import React, { useEffect, useState } from 'react'
import { Center, Spinner, useColorModeValue } from '@chakra-ui/react'

type LoadingProps = {
  timeout?: number
}

/**
 * Displays the given loading component or a Spinner after the given timeout or one second.
 * @param timeout Number, Optional - The timeout in seconds before the component appears. Default value is 1 sec.
 * @param children ReactNode, Optional - The component to be displayed instead of a brand colored spinner.
 * @constructor
 */
export const Loading: React.FC<LoadingProps> = ({ timeout, children }) => {
  const [show, setShow] = useState<boolean>(false)
  useEffect(() => {
    setTimeout(() => {
      setShow(true)
    }, (timeout ?? 1) * 1000) // use ?? instead of || because the latter will give false value if timeout is set to 0
  }, [setTimeout, timeout])
  if (!show) return null
  return children ? (
    <>{children}</>
  ) : (
    <Center>
      <Spinner color={useColorModeValue('brand.500', 'brand.600')} size="xl" thickness="0.3rem" />
    </Center>
  )
}

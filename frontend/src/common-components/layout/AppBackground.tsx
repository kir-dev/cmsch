import { FC, PropsWithChildren } from 'react'
import { Box, useColorModeValue } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export const AppBackground: FC<PropsWithChildren> = ({ children }) => {
  const config = useConfigContext()
  const backgroundImage = useColorModeValue(
    `url(${config.components.style.lightBackgroundUrl})`,
    `url(${config.components.style.darkBackgroundUrl})`
  )
  return (
    <>
      <Box
        position="fixed"
        zIndex={-9999999}
        height="100vh"
        width="100vw"
        bgImage={backgroundImage}
        bgRepeat={'no-repeat'}
        bgSize={'cover'}
        bgPosition={'center'}
      ></Box>
      {children}
    </>
  )
}

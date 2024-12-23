import { FC, PropsWithChildren } from 'react'
import { Box, useColorModeValue } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'

export const AppBackground: FC<PropsWithChildren> = ({ children }) => {
  const config = useConfigContext()
  const textColor = useColorModeValue(config.components.style.lightTextColor, config.components.style.darkTextColor)
  const background = useColorModeValue(config.components.style.lightBackgroundColor, config.components.style.darkBackgroundColor)
  let backgroundImage = useColorModeValue(
    `url(${config.components.style.lightBackgroundUrl})`,
    `url(${config.components.style.darkBackgroundUrl})`
  )

  if (window.innerWidth <= 768)
    backgroundImage = useColorModeValue(
      `url(${config.components.style.lightMobileBackgroundUrl})`,
      `url(${config.components.style.darkMobileBackgroundUrl})`
    )

  return (
    <>
      <Box
        position="fixed"
        zIndex={-9999999}
        height="100vh"
        width="100vw"
        bg={background}
        color={textColor}
        bgImage={backgroundImage}
        bgRepeat={'no-repeat'}
        bgSize={'cover'}
        bgPosition={'center'}
      ></Box>
      {children}
    </>
  )
}

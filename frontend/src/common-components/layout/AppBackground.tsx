import { FC, PropsWithChildren } from 'react'
import { Box } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useColorModeValue } from '../../components/ui/color-mode'

export const AppBackground: FC<PropsWithChildren> = ({ children }) => {
  const config = useConfigContext()
  const textColor = useColorModeValue(config.components.style.lightTextColor, config.components.style.darkTextColor)
  const background = useColorModeValue(config.components.style.lightBackgroundColor, config.components.style.darkBackgroundColor)

  const backgroundImage = useColorModeValue(
    `url(${config.components.style.lightBackgroundUrl})`,
    `url(${config.components.style.darkBackgroundUrl})`
  )

  const mobileBackgroundImage = useColorModeValue(
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
        bgImage={{ base: mobileBackgroundImage, md: backgroundImage }}
        bgRepeat={'no-repeat'}
        bgSize={'cover'}
        bgPos={'center'}
      ></Box>
      {children}
    </>
  )
}

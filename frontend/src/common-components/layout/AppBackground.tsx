import { Box, useColorModeValue } from '@chakra-ui/react'
import type { FC, PropsWithChildren } from 'react'
import { usePersistentStyleSetting } from '../../util/configs/themeStyle.config.ts'

export const AppBackground: FC<PropsWithChildren> = ({ children }) => {
  const { persistentStyle: theme } = usePersistentStyleSetting()
  const textColor = useColorModeValue(theme?.lightTextColor, theme?.darkTextColor)
  const background = useColorModeValue(theme?.lightBackgroundColor, theme?.darkBackgroundColor)

  const backgroundImage = useColorModeValue(`url(${theme?.lightBackgroundUrl})`, `url(${theme?.darkBackgroundUrl})`)
  const mobileBackgroundImage = useColorModeValue(`url(${theme?.lightMobileBackgroundUrl})`, `url(${theme?.darkMobileBackgroundUrl})`)

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
        bgPosition={'center'}
      ></Box>
      <Box color={textColor}>{children}</Box>
    </>
  )
}

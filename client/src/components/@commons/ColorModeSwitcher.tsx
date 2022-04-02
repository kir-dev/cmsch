import { MoonIcon, SunIcon } from '@chakra-ui/icons'
import { IconButton, IconButtonProps, useColorMode, useColorModeValue } from '@chakra-ui/react'
import { FC } from 'react'

type ColorModeSwitcherProps = Omit<IconButtonProps, 'aria-label'>

export const ColorModeSwitcher: FC<ColorModeSwitcherProps> = (props) => {
  const { toggleColorMode } = useColorMode()

  return (
    <IconButton
      aria-label="Sötét-világos mód váltás"
      icon={useColorModeValue(<SunIcon w={5} h={5} />, <MoonIcon w={5} h={5} />)}
      onClick={toggleColorMode}
      variant="ghost"
      {...props}
    />
  )
}

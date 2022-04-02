import { IconButton, IconButtonProps, useColorMode, useColorModeValue } from '@chakra-ui/react'
import * as React from 'react'
import { MoonIcon, SunIcon } from '@chakra-ui/icons'

type ColorModeSwitcherProps = Omit<IconButtonProps, 'aria-label'>

export const ColorModeSwitcher: React.FC<ColorModeSwitcherProps> = (props) => {
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

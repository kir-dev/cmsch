import { Icon, IconButton, IconButtonProps, useColorMode, useColorModeValue } from '@chakra-ui/react'
import { FC } from 'react'
import { FaMoon, FaSun } from 'react-icons/fa'

type ColorModeSwitcherProps = Omit<IconButtonProps, 'aria-label'>

export const ColorModeSwitcher: FC<ColorModeSwitcherProps> = (props) => {
  const { toggleColorMode } = useColorMode()
  return (
    <IconButton
      aria-label="Sötét-világos mód váltás"
      icon={useColorModeValue(<Icon as={FaSun} w={5} h={5} />, <Icon as={FaMoon} w={5} h={5} />)}
      onClick={toggleColorMode}
      variant="ghost"
      {...props}
    />
  )
}

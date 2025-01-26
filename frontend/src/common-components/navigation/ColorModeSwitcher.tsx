import { MoonIcon, SunIcon } from '@chakra-ui/icons'
import { IconButton, IconButtonProps } from '@chakra-ui/react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useColorMode, useColorModeValue } from '../../components/ui/color-mode.tsx'

type Props = Omit<IconButtonProps, 'aria-label'>

export const ColorModeSwitcher = (props: Props) => {
  const { toggleColorMode } = useColorMode()
  const config = useConfigContext()

  return config?.components.style.darkModeEnabled ? (
    <IconButton aria-label="Sötét-világos mód váltás" onClick={toggleColorMode} variant="ghost" {...props}>
      {useColorModeValue(<SunIcon w={5} h={5} />, <MoonIcon w={5} h={5} />)}
    </IconButton>
  ) : null
}

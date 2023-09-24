import { useDisclosure } from '@chakra-ui/hooks'
import { chakra, useColorModeValue } from '@chakra-ui/react'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'
import { useEffect } from 'react'

type Props = {
  text: string
}

export const SpoilerText = ({ text }: Props) => {
  const { isOpen, onToggle, onClose } = useDisclosure()
  const config = useConfigContext()

  useEffect(() => {
    onClose()
  }, [text])

  const hiddenColor = useColorModeValue(config.components.style.lightTextColor, config.components.style.darkTextColor)
  return (
    <chakra.label onClick={onToggle} bgColor={isOpen ? undefined : hiddenColor} color={isOpen ? undefined : hiddenColor}>
      {text}
    </chakra.label>
  )
}

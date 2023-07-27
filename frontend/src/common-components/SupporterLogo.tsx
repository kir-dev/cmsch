import { Image } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'

interface SupporterLogoProps {
  name: 'bme' | 'schonherz' | 'schdesign' | 'vik'
}

export function SupporterLogo({ name }: SupporterLogoProps) {
  const fileVariant = useColorModeValue('light', 'dark')
  const fileName = `${name}_${fileVariant}`
  return <Image src={`/img/supporters/${fileName}.svg`} maxH={20} maxW={32} />
}

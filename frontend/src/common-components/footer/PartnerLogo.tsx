import { Image } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'

interface PartnerLogoProps {
  name: 'bme' | 'schonherz' | 'schdesign' | 'vik'
}

export function PartnerLogo({ name }: PartnerLogoProps) {
  const fileVariant = useColorModeValue('light', 'dark')
  const fileName = `${name}_${fileVariant}`
  return <Image m={5} src={`/img/supporters/${fileName}.svg`} maxH={20} maxW={32} />
}

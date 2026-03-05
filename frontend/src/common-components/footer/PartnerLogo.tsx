import { useColorModeValue } from '@/util/core-functions.util'

interface PartnerLogoProps {
  name: 'bme' | 'schonherz' | 'schdesign' | 'vik'
}

export function PartnerLogo({ name }: PartnerLogoProps) {
  const fileVariant = useColorModeValue('light', 'dark')
  const fileName = `${name}_${fileVariant}`
  return <img className="m-5 max-h-20 max-w-[8rem] object-contain" src={`/img/supporters/${fileName}.svg`} alt={name} />
}

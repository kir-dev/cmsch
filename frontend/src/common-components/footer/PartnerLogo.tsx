import bmeDark from '@/assets/img/supporters/bme_dark.svg'
import bmeLight from '@/assets/img/supporters/bme_light.svg'
import schdesignDark from '@/assets/img/supporters/schdesign_dark.svg'
import schdesignLight from '@/assets/img/supporters/schdesign_light.svg'
import schonherzDark from '@/assets/img/supporters/schonherz_dark.svg'
import schonherzLight from '@/assets/img/supporters/schonherz_light.svg'
import vikDark from '@/assets/img/supporters/vik_dark.svg'
import vikLight from '@/assets/img/supporters/vik_light.svg'
import { useColorModeValue } from '@/util/core-functions.util'

const partnerLogos = {
  bme: { light: bmeLight, dark: bmeDark },
  schonherz: { light: schonherzLight, dark: schonherzDark },
  schdesign: { light: schdesignLight, dark: schdesignDark },
  vik: { light: vikLight, dark: vikDark }
} as const

interface PartnerLogoProps {
  name: 'bme' | 'schonherz' | 'schdesign' | 'vik'
}

export function PartnerLogo({ name }: PartnerLogoProps) {
  const fileVariant = useColorModeValue('light', 'dark')
  return <img className="m-5 max-h-20 max-w-32 object-contain" src={partnerLogos[name][fileVariant]} alt={name} />
}

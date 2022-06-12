import { IconType } from 'react-icons'
import { FaFacebook, FaInstagram } from 'react-icons/fa'

export type SocialPage = {
  href: string
  label: string
  icon: IconType
}

/** TODO: Added temporarily, to be removed later */
export const SOCIAL_PAGES: SocialPage[] = [
  {
    href: 'https://www.facebook.com/events/697152491692648',
    label: 'GólyaKörTe Facebook esemény',
    icon: FaFacebook
  },
  {
    href: 'https://www.instagram.com/golyakortevik',
    label: 'GólyaKörTe Instagram',
    icon: FaInstagram
  }
]

import { FaFacebook, /*FaGithub, FaYoutube, FaTwitter*/ FaInstagram } from 'react-icons/fa'
import { SocialPage } from 'types/SocialPage'

export const socialPages: SocialPage[] = [
  /*{
    href: GITHUB_ORG_URL,
    label: 'GólyaKörTe GitHub',
    icon: FaGithub
  },
  {
    href: YOUTUBE_CHANNEL_URL,
    label: 'GólyaKörTe YouTube',
    icon: FaYoutube
  },*/
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
  /*{
    href: '',
    label: 'GólyaKörTe Twitter',
    icon: FaTwitter
  }*/
]

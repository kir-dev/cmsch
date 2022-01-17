import { NavItem } from '../types/NavItem'

export const NAV_ITEMS: Array<NavItem> = [
  {
    label: 'Főoldal',
    href: '/',
    children: [
      {
        label: 'Asd',
        href: '/'
      }
    ]
  },
  {
    label: 'Reszortok',
    href: '/reszortok'
  },
  {
    label: 'Körök',
    href: '/korok'
  },
  {
    label: 'Profil',
    href: '/profil'
  }
]

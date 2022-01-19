import { NavItem } from '../types/NavItem'

export const NAV_ITEMS: Array<NavItem> = [
  {
    label: 'Események',
    href: '/esemenyek'
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
    label: 'Feladatok',
    href: undefined,
    loginRequired: true,
    children: [
      {
        label: 'Bucketlist',
        href: '/bucketlist',
        loginRequired: true
      },
      {
        label: 'Riddle',
        href: '/riddleok',
        loginRequired: true
      },
      {
        label: 'QR',
        href: '/qr',
        loginRequired: true
      }
    ]
  },
  {
    label: 'Profil',
    href: '/profil',
    loginRequired: true
  }
]

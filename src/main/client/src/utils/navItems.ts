import { NavItem } from '../types/NavItem'

export const NAV_ITEMS: Array<NavItem> = [
  {
    label: 'Főoldal',
    href: '/'
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
    href: '/profil',
    loginRequired: true,
    children: [
      {
        label: 'Bucketlist',
        href: '/bucketlist',
        loginRequired: true
      },
      {
        label: 'QR',
        href: '/qr',
        loginRequired: true
      }
    ]
  }
]

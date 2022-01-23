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
    label: 'Feladatok',
    href: '/profil',
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
    loginRequired: true,
    children: [{ label: 'Profilom', href: '/profil', loginRequired: true }]
  }
]

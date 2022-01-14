import { NavItem } from '../types/NavItem'

export const NAV_ITEMS: Array<NavItem> = [
  {
    label: 'Blog',
    href: '/blog',
    children: [
      {
        label: 'Legújabb posztok',
        href: '/blog'
      },
      {
        label: 'Archívum',
        href: '/archive'
      }
    ]
  },
  {
    label: 'Rólunk',
    href: '/about'
  },
  {
    label: 'Csapatunk',
    href: '/members'
  },
  {
    label: 'Projektjeink',
    href: '/projects'
  },
  {
    label: 'Tanfolyam',
    href: '/courses'
  }
]

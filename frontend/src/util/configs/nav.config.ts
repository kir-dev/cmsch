import { FaHome, FaSignInAlt, FaUserCircle } from 'react-icons/fa'
import { IconType } from 'react-icons/lib'

interface INavItem {
  icon: IconType
  label: string
  path: string | undefined
  shouldBeShown: (isLoggedIn: boolean) => boolean
  children?: INavItem[]
}

class NavItem implements INavItem {
  public icon: IconType
  public label: string
  public path: string
  public children?: INavItem[] = undefined

  constructor({ icon, label, path }: { icon: IconType; label: string; path: string }) {
    this.icon = icon
    this.label = label
    this.path = path
  }

  public shouldBeShown = (isLoggedIn: boolean) => true
}

const Item1 = new NavItem({
  icon: FaUserCircle,
  label: 'ItemNoChildren',
  path: '#'
})
Item1.shouldBeShown = (isLoggedIn: boolean) => isLoggedIn

const Item2 = new NavItem({
  icon: FaUserCircle,
  label: 'Item w/ Children',
  path: '#'
})
Item2.children = [
  new NavItem({
    icon: FaUserCircle,
    label: 'Child',
    path: '#'
  })
]

const LoginItem = new NavItem({
  icon: FaSignInAlt,
  label: 'Log in',
  path: '/login'
})
LoginItem.shouldBeShown = (isLoggedIn: boolean) => !isLoggedIn

const NAV_ITEMS: INavItem[] = [
  new NavItem({
    icon: FaHome,
    label: 'Home',
    path: '/'
  }),
  Item1,
  Item2,
  LoginItem
]

export { NAV_ITEMS, NavItem }
export type { INavItem }

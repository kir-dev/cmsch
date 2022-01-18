export interface NavItem {
  label: string
  children?: Array<NavItem>
  href: string
  loginRequired?: boolean
}

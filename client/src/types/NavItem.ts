export interface NavItem {
  label: string
  children?: Array<NavItem>
  href: string | undefined
  loginRequired?: boolean
}

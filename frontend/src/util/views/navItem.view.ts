export interface NavItemView {
  label: string
  children?: Array<NavItemView>
  href: string | undefined
  loginRequired?: boolean
}

import { RoleType } from './profile.view'

export type ExtraPageView = {
  url: string
  title: string
  content: string
  permissionToEdit: string
  minRole: keyof typeof RoleType
  ogTitle: string
  ogImage: string
  ogDescription: string
}

export type ExtraPageDto = {
  page: ExtraPageView
}

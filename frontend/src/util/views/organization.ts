export interface Organization {
  id: number
  name: string
  hideName?: boolean
  shortDescription?: string
  descriptionParagraphs?: string
  website?: string
  logo?: string
  darkLogo?: string
  established?: string
  email?: string
  members?: number
  color?: string
  interests?: string[]
  facebook?: string
  instagram?: string
  application?: string
  imageIds?: string[]
  videoIds?: string[]
  visible?: boolean
}

export interface Community extends Organization {
  resortId?: number
  resortName?: string
  searchKeywords?: string[]
  svgMapId?: string[]
}

export type OrganizationListItem = Pick<Organization, 'id' | 'name' | 'shortDescription' | 'logo' | 'members'>
export type CommunityListItem = Pick<Community, 'id' | 'name' | 'shortDescription' | 'logo' | 'members'>

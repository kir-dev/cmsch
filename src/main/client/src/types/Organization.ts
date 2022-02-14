export interface Organization {
  id: string
  name: string
  /** Should hide name on their own page, logo is enough */
  hideName?: boolean
  shortDescription?: string
  descriptionParagraphs?: string[] | string
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
}

export interface Community extends Organization {
  resortId?: string
  searchKeywords?: string[]
}

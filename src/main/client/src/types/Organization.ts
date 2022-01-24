export interface Organization {
  id: string
  name: string
  shortDescription?: string
  description?: string
  website?: string
  logo?: string
  established?: string
  email?: string
  members?: number
  color?: string
  interests?: string[]
  facebook?: string
  instagram?: string
  application?: string
}

export interface Community extends Organization {
  images?: string[]
  resortId?: string
}

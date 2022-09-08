export type TeamView = {
  id: string
  name: string
  points: number
  members: TeamMemberView[]
  applicants: TeamMemberView[]
}

export type TeamMemberView = {
  name: string
  id: string
  isAdmin: boolean
}

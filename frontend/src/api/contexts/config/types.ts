export interface ConfigDto {
  role: string
  menu: Menu[]
  components: Components
  theme: Theme
}

export interface Theme {
  brandColor: string
}

export interface Components {
  countdown: Countdown
  extraPage: ExtraPage
  impressum: Impressum
  news: News
  riddle: News
  task: News
  token: Token
}

export interface Countdown {
  title: string
  enabled: string
  topMessage: string
  timeToCountTo: string
  informativeOnly: string
  imageUrl: string
  blurredImage: string
}

export interface ExtraPage {}

export interface Impressum {
  title: string
  topMessage: string
  developerSchamiUrl: string
  developerBalintUrl: string
  developerLaciUrl: string
  developerBeniUrl: string
  developerTriszUrl: string
  developerSamuUrl: string
  developerDaniUrl: string
  developerMateUrl: string
  developersBottomMessage: string
  leadOrganizers: string
  leadOrganizersMessage: string
  otherOrganizers: string
  otherOrganizersMessage: string
}

export interface News {
  title: string
}

export interface Token {
  title: string
  collectRequired: string
  minTokenMsg: string
  minTokenAchievedMsg: string
  defaultIcon: string
  defaultTestIcon: string
}

export interface Menu {
  url: string
  name: string
  external: boolean
  children: Menu[]
}

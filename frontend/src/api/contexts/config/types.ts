export interface ConfigDto {
  role: string
  menu: Menu[]
  components: Components
}

export interface Components {
  app: App
  style: Style
  userHandling: Debt
  countdown: Countdown
  debt: Debt
  event: Event
  extraPage: Debt
  groupselection: Debt
  home: Home
  impressum: Impressum
  leaderboard: Leaderboard
  location: Debt
  login: Debt
  news: Home
  profile: Profile
  riddle: Home
  signup: Debt
  task: Home
  token: Token
}

export interface App {
  warningMessage: string
  warningLevel: 'success' | 'info' | 'warning' | 'error'
  siteName: string
  defaultComponent: string
  siteLogoUrl: string
  faviconUrl: string
  hostLogo: string
  hostAlt: string
  hostWebsiteUrl: string
  facebookUrl: string
  instagramUrl: string
  footerMessage: string
  devLogo: string
  devAlt: string
  devWebsiteUrl: string
  sponsorsEnabled: string
  sponsorLogoUrls: string
  sponsorAlts: string
  sponsorWebsiteUrls: string
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

export interface Debt {}

export interface Event {
  title: string
  seekToCurrentCurrent: string
  separateDays: string
  topMessage: string
  enableDetailedView: string //TODO ez miért string??
}

export interface Home {
  title: string
}

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
  leadOrganizers: string // JSONified Organizer[]
  leadOrganizersMessage: string
  otherOrganizers: string // JSONified Organizer[]
  otherOrganizersMessage: string
}

export interface Organizer {
  name: string
  roles: string
  avatar: string
}

export interface Leaderboard {
  leaderboardEnabled: string
  leaderboardFrozen: string
}

export interface Profile {
  title: string
  showTasks: string
  taskCounterName: string
  showRiddles: string
  riddleCounterName: string
  showTokens: string
  tokenCounterName: string
  showFullName: string
  showGuild: string
  showNeptun: string
  showProfilePicture: string
  showQr: string
  showGroupName: string
  groupTitle: string
  allowGroupSelect: string
  messageBoxContent: string
  messageBoxLevel: string
  showGroupLeadersLocations: string
  showMinimumToken: string
  minTokenMsg: string
  minTokenAchievedMsg: string
}

export interface Style {
  backgroundColor: string
  textColor: string
  textColorAccent: string
  brandingColor: string
  backgroundUrl: string
  mobileBackgroundUrl: string
  mainFontName: string
  mainFontCdn: string
  mainFontWeight: string
  displayFontName: string
  displayFontCdn: string
  displayFontWeight: string
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
  name: string
  url: string
  external: boolean
  children: Menu[]
}

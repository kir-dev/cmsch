export interface ConfigDto {
  role: string
  menu: Menu[]
  components: Components
}

export interface Menu {
  name: string
  url: string
  external: boolean
  children: Menu[]
}

export interface Components {
  app: App
  style: Style
  userHandling: UserHandling
  countdown: Countdown
  debt: Debt
  event: Event
  extraPage: ExtraPage
  groupselection: GroupSelection
  home: Home
  impressum: Impressum
  leaderboard: Leaderboard
  location: Location
  login: Login
  news: News
  profile: Profile
  riddle: Riddle
  signup: Signup
  task: Task
  token: Token
}

export interface App {
  warningMessage: string
  warningLevel: 'success' | 'info' | 'warning' | 'error'
  minimalisticFooter: boolean
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
  sponsorsEnabled: boolean
  sponsorLogoUrls: string
  sponsorAlts: string
  sponsorWebsiteUrls: string
}

export interface Countdown {
  title: string
  enabled: boolean
  showOnly: boolean
  topMessage: string
  timeToCountTo: number
  informativeOnly: boolean
  imageUrl: string
  blurredImage: boolean
}

export interface GroupSelection {
  selectionEnabled: boolean
}

export interface Event {
  title: string
  seekToCurrentCurrent: boolean
  separateDays: boolean
  topMessage: string
  enableDetailedView: boolean
  filterByCategory: boolean
  filterByLocation: boolean
  filterByDay: boolean
}

export interface Home {
  title: string
  welcomeMessage: string
  content: string
  youtubeVideoId: string
}

export interface News {
  title: string
  showDetails: boolean
}

export interface Riddle {
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
  leadOrganizers: Organizer[] // JSONified Organizer[]
  leadOrganizersMessage: string
  otherOrganizers: Organizer[] // JSONified Organizer[]
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
  maxGroupEntryToShow: number
  maxUserEntryToShow: number
  minScoreToShow: number
  showGroupBoard: boolean
  showScores: boolean
  showUserBoard: boolean
  title: string
}

export interface Profile {
  title: string
  showTasks: boolean
  taskCounterName: string
  showRiddles: boolean
  riddleCounterName: string
  showTokens: boolean
  tokenCounterName: string
  showFullName: boolean
  showGuild: boolean
  showMajor: boolean
  showAlias: boolean
  showGroup: boolean
  showNeptun: boolean
  showEmail: boolean
  showProfilePicture: boolean
  showQr: boolean
  groupTitle: string
  messageBoxContent: string
  messageBoxLevel: string
  showGroupName: boolean
  showGroupLeadersLocations: boolean
  showMinimumToken: boolean
  minTokenMsg: string
  minTokenAchievedMsg: string
  profileIncomplete: string
  profileComplete: string
}

export interface Style {
  lightBackgroundColor: string
  lightContainerColor: string
  lightTextColor: string
  lightBrandingColor: string
  lightBackgroundUrl: string
  lightMobileBackgroundUrl: string
  lightLogoUrl: string
  darkModeEnabled: boolean
  deviceTheme: boolean
  forceDarkMode: boolean
  darkBackgroundColor: string
  darkContainerColor: string
  darkTextColor: string
  darkBackgroundUrl: string
  darkMobileBackgroundUrl: string
  darkLogoUrl: string
  mainFontName: string
  mainFontCdn: string
  mainFontWeight: number
  displayFontName: string
  displayFontCdn: string
  displayFontWeight: number
}

export interface Task {
  title: string
  profileRequiredTitle: string
  profileRequiredMessage: string
  regularTitle: string
  regularMessage: string
  resubmissionEnabled: boolean
}

export interface Token {
  title: string
  collectRequired: string
  minTokenMsg: string
  minTokenAchievedMsg: string
  defaultIcon: string
  defaultTestIcon: string
}

export interface Debt {}
export interface UserHandling {}
export interface ExtraPage {}
export interface Location {}
export interface Login {
  onlyBmeProvider: boolean
  langLoginMenu: string
}
export interface Signup {
  langTooEarly: string
  langTooLate: string
  langNotEnabled: string
  langFull: string
  langNotFound: string
  langSubmitted: string
  langRejected: string
  langAccepted: string
  langGroupInsufficient: string
  langNoSubmission?: string
}

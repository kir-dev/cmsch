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
  race: Race
  riddle: Riddle
  signup: Signup
  task: Task
  team: Team
  token: Token
  qrFight: QrFight
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
  showEvents: boolean
}

export interface News {
  title: string
  showDetails: boolean
}

export interface Race {
  title: string
  visible: boolean
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
  leaderboardDetailsEnabled: boolean
  leaderboardFrozen: string
  maxGroupEntryToShow: number
  maxUserEntryToShow: number
  minScoreToShow: number
  showGroupBoard: boolean
  showScores: boolean
  showGroupOfUser: boolean
  showUserBoard: boolean
  title: string
}

export interface Profile {
  aliasChangeEnabled: boolean
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
  showQr: boolean
  qrTitle: string
  showEmail: boolean
  showProfilePicture: boolean
  groupTitle: string
  groupLeadersHeader: string
  messageBoxContent: string
  messageBoxLevel: string
  showGroupName: boolean
  showGroupLeadersLocations: boolean
  showMinimumToken: boolean
  minTokenMsg: string
  minTokenAchievedMsg: string
  profileIncomplete: string
  showIncompleteProfile: boolean
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
  collectFeature: boolean
  collectType: string
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
  authschPromoted: boolean
  googleSsoEnabled: boolean
  bottomMessage: string
}

export interface Team {
  myTitle: string
  title: string
  createTitle: string
  teamCreationTopMessage: string
  creationEnabled: boolean
  joinEnabled: boolean
  racesByDefault: boolean
  selectableByDefault: boolean
  adminTitle: string
  togglePermissionEnabled: boolean
  kickEnabled: boolean
  promoteLeadershipEnabled: boolean
  showTeamDetails: boolean
  showTeamMembersPublicly: boolean
  showTeamScore: boolean
  showTeamScoreDetailsButton: boolean
}

export interface QrFight {
  title: string
  enabled: boolean
  topMessage: string
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

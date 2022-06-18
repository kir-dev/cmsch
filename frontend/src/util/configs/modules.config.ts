import { FunctionComponent } from 'react'
import { RiddleModule } from '../../route-modules/Riddle.module'
import { TaskModule } from '../../route-modules/Task.module'
import { NewsModule } from '../../route-modules/News.module'
import { ProfileModule } from '../../route-modules/Profile.module'
import { CommunitiesModule } from '../../route-modules/Communities.module'
import { TokenModule } from '../../route-modules/Token.module'

export enum AvailableModules {
  RIDDLE = 'RIDDLE',
  TASK = 'TASK',
  IMPRESSUM = 'IMPRESSUM',
  NEWS = 'NEWS',
  PROFILE = 'PROFILE',
  COMMUNITIES = 'COMMUNITIES',
  TOKEN = 'TOKEN'
}

export const RoutesForModules: Record<AvailableModules, FunctionComponent> = {
  [AvailableModules.RIDDLE]: RiddleModule,
  [AvailableModules.TASK]: TaskModule,
  [AvailableModules.IMPRESSUM]: TaskModule,
  [AvailableModules.NEWS]: NewsModule,
  [AvailableModules.PROFILE]: ProfileModule,
  [AvailableModules.COMMUNITIES]: CommunitiesModule,
  [AvailableModules.TOKEN]: TokenModule
}

export function GetRoutesForModules(modules: AvailableModules[]) {
  return modules.map((m) => RoutesForModules[m]([]))
}

export const EnabledModules: AvailableModules[] = [
  AvailableModules.PROFILE,
  AvailableModules.RIDDLE,
  AvailableModules.COMMUNITIES,
  AvailableModules.TOKEN
]

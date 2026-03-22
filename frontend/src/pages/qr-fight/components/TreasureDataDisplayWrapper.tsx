import Markdown from '@/common-components/Markdown'
import { useOpaqueBackground } from '@/util/core-functions.util'
import { LevelStatus, type QrTreasureHuntDto } from '@/util/views/qrFight.view'
import isEmpty from 'lodash/isEmpty'
import { LevelDataDisplay } from './LevelDataDisplay'
import { LevelStatusBadge } from './LevelStatusBadge'

interface TreasureDataDisplayWrapperProps {
  level: QrTreasureHuntDto
}

export function TreasureDataDisplayWrapper({ level }: TreasureDataDisplayWrapperProps) {
  const backgroundColor = useOpaqueBackground(3)

  return (
    <div
      className="flex flex-col space-y-5 p-5 mt-5 rounded-xl"
      style={{
        backgroundColor: backgroundColor,
        opacity: level.status === LevelStatus.COMPLETED || level.status === LevelStatus.OPEN ? 1 : 0.2
      }}
    >
      <div className="flex flex-row justify-start w-full">
        <div className="flex flex-col items-start space-y-2">
          <h2 className="m-0 text-2xl font-bold">{level.name}</h2>
          <LevelStatusBadge levelStatus={level.status} />
          <p>Birtokló: {level.owners}</p>
          <p>A te csapatodnak van: {level.tokenCount}db</p>
          <Markdown text={level.description} />
        </div>
      </div>
      {!isEmpty(level.teams) && <LevelDataDisplay teams={level.teams} />}
      <div className="flex flex-row justify-start w-[90%] m-5">
        <div className="flex flex-col items-start space-y-1">
          <h3 className="text-xl font-bold m-0">Kincsek rejtekhelyei:</h3>
          {level.foundTokens.map((token, index) => (
            <p key={index}>- {token}</p>
          ))}
        </div>
      </div>
    </div>
  )
}

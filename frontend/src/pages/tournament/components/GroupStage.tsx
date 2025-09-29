import { TournamentStageView } from '../../../util/views/tournament.view.ts'

interface GroupStageProps {
  stage: TournamentStageView
}

const GroupStage: React.FC<GroupStageProps> = ({ stage }: GroupStageProps) => {
  const groups = Array.from({ length: stage.groupCount }, (_, i) => i).map((groupIndex) => {
    return stage.participants.filter((participant) => participant.seed % stage.groupCount === groupIndex)
  })

  return <div>GroupStage</div>
}

export default GroupStage

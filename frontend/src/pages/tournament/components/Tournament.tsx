import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useTournamentCancelMutation } from '@/api/hooks/tournament/actions/useTournamentCancelMutation.ts'
import { useTournamentJoinMutation } from '@/api/hooks/tournament/actions/useTournamentJoinMutation.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CustomTabButton } from '@/common-components/CustomTabButton.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage.tsx'
import { Button } from '@/components/ui/button.tsx'
import { Tabs, TabsContent, TabsList } from '@/components/ui/tabs.tsx'
import { useToast } from '@/hooks/use-toast.ts'
import GroupStage from '@/pages/tournament/components/GroupStage.tsx'
import KnockoutStage from '@/pages/tournament/components/KnockoutStage.tsx'
import type { TournamentDetailsView } from '@/util/views/tournament.view.ts'
import {
  TournamentCancelResponseMessages,
  TournamentCancelResponses,
  TournamentJoinResponseMessages,
  TournamentJoinResponses
} from '@/util/views/tournament.view.ts'
import { Undo2 } from 'lucide-react'

interface TournamentProps {
  tournament: TournamentDetailsView
  refetch?: () => void
}

const Tournament = ({ tournament, refetch = () => {} }: TournamentProps) => {
  const config = useConfigContext()
  const tournamentComponent = config?.components?.tournament
  const joinMutation = useTournamentJoinMutation()
  const cancelMutation = useTournamentCancelMutation()
  const { toast } = useToast()

  const joinActionResponseCallback = (response: TournamentJoinResponses) => {
    if (response == TournamentJoinResponses.OK) {
      toast({ variant: 'default', title: TournamentJoinResponseMessages[response] })
      refetch()
    } else {
      toast({ variant: 'destructive', title: TournamentJoinResponseMessages[response] })
    }
  }
  const cancelActionResponseCallback = (response: TournamentCancelResponses) => {
    if (response == TournamentCancelResponses.OK) {
      toast({ variant: 'default', title: TournamentCancelResponseMessages[response] })
      refetch()
    } else {
      toast({ variant: 'destructive', title: TournamentCancelResponseMessages[response] })
    }
  }

  const joinTournament = () => {
    if (tournament.tournament.joinEnabled) {
      joinMutation.mutate(tournament.tournament.id, {
        onSuccess: (response: TournamentJoinResponses) => {
          joinActionResponseCallback(response)
          if (response === TournamentJoinResponses.OK) {
            refetch()
          }
        },
        onError: () => {
          toast({ variant: 'destructive', title: 'Hiba történt a versenyre való jelentkezés során.' })
        }
      })
    }
  }
  const cancelJoinTournament = () => {
    if (tournament.tournament.joinCancellable) {
      cancelMutation.mutate(tournament.tournament.id, {
        onSuccess: (response: TournamentCancelResponses) => {
          cancelActionResponseCallback(response)
          if (response === TournamentCancelResponses.OK) {
            refetch()
          }
        },
        onError: () => {
          toast({ variant: 'destructive', title: 'Hiba történt a versenyre való jelentkezés visszavonása során.' })
        }
      })
    }
  }

  if (!tournamentComponent) return <ComponentUnavailable />

  return (
    <CmschPage title={tournament.tournament.title}>
      <div className="mb-5">
        <h1 className="mb-5 text-4xl font-bold tracking-tight">{tournament.tournament.title}</h1>
        <p className="mb-2">{tournament.tournament.description}</p>
        <p className="mb-2">{tournament.tournament.location}</p>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          {tournament.tournament.joined ? (
            <Button
              className="flex items-center gap-2 bg-primary text-primary-foreground"
              onClick={() => cancelJoinTournament()}
              disabled={!tournament.tournament.joinCancellable}
            >
              <Undo2 className="h-4 w-4" /> Jelentkezés visszavonása
            </Button>
          ) : (
            <Button
              className="flex items-center gap-2 bg-primary text-primary-foreground"
              onClick={() => joinTournament()}
              disabled={!tournament.tournament.joinEnabled}
            >
              Jelentkezés a versenyre
            </Button>
          )}
        </div>
        <Tabs defaultValue="participants" className="w-full">
          <TabsList className="mb-5 flex w-full flex-wrap justify-start">
            <CustomTabButton value="participants">Résztvevők</CustomTabButton>
            {tournament.stages.map((stage, index) => (
              <CustomTabButton value={`stage-${index}`}>{stage.name}</CustomTabButton>
            ))}
          </TabsList>

          <TabsContent value="participants">
            <div className="flex flex-col gap-0">
              {tournament.tournament.participants.map((participant) => (
                <div key={participant.teamId}>
                  <h3 className="mb-3 text-xl tracking-tight">{participant.teamName}</h3>
                </div>
              ))}
            </div>
          </TabsContent>

          {tournament.stages.map((stage, index) => (
            <TabsContent key={stage.id} value={`stage-${index}`}>
              {stage.type === 'KNOCKOUT' && <KnockoutStage key={stage.id} stage={stage} />}
              {stage.type === 'STAGE' && <GroupStage key={stage.id} stage={stage} />}
            </TabsContent>
          ))}
        </Tabs>
      </div>
    </CmschPage>
  )
}

export default Tournament

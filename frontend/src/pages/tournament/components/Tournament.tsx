import { useConfigContext } from '@/api/contexts/config/ConfigContext.tsx'
import { useTournamentJoinMutation } from '@/api/hooks/tournament/actions/useTournamentJoinMutation.ts'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable.tsx'
import { CmschPage } from '@/common-components/layout/CmschPage.tsx'
import { useToast } from '@/hooks/use-toast.ts'
import type { TournamentDetailsView } from '@/util/views/tournament.view.ts'
import { TournamentResponseMessages, TournamentResponses } from '@/util/views/tournament.view.ts'
import { useState } from 'react'
import KnockoutStage from './KnockoutStage.tsx'

interface TournamentProps {
  tournament: TournamentDetailsView
  refetch?: () => void
}

const Tournament = ({ tournament, refetch = () => {} }: TournamentProps) => {
  const config = useConfigContext()
  const tournamentComponent = config?.components?.tournament
  const joinMutation = useTournamentJoinMutation()
  const { toast } = useToast()

  const actionResponseCallback = (response: TournamentResponses) => {
    if (response == TournamentResponses.OK) {
      toast({ variant: 'default', title: TournamentResponseMessages[response] })
      refetch()
    } else {
      toast({ variant: 'destructive', title: TournamentResponseMessages[response] })
    }
  }

  const joinTournament = () => {
    if (tournament.tournament.joinEnabled) {
      joinMutation.mutate(tournament.tournament.id, {
        onSuccess: (response: TournamentResponses) => {
          actionResponseCallback(response)
          if (response === TournamentResponses.OK) {
            refetch()
          }
        },
        onError: () => {
          toast({ variant: 'destructive', title: 'Hiba történt a versenyre való jelentkezés során.' })
        }
      })
    }
  }

  const [tabIndex, setTabIndex] = useState(0)

  const onTabSelected = (i: number) => {
    setTabIndex(i)
  }

  if (!tournamentComponent) return <ComponentUnavailable />

  return (
    <CmschPage title={tournament.tournament.title}>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16, padding: 16, maxWidth: '100%', margin: '0 auto' }}>
        <h1 style={{ margin: 0 }}>{tournament.tournament.title}</h1>
        <p style={{ margin: 0 }}>{tournament.tournament.description}</p>
        <p style={{ margin: 0 }}>{tournament.tournament.location}</p>
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          {tournament.tournament.joinEnabled && (
            <button
              type="button"
              onClick={joinTournament}
              disabled={tournament.tournament.isJoined}
              style={{ display: 'inline-flex', alignItems: 'center', gap: 8 }}
            >
              Jelentkezés a versenyre
            </button>
          )}
          {tournament.tournament.isJoined && (
            <button type="button" disabled>
              Jelentkezve
            </button>
          )}
        </div>
        <div>
          <div role="tablist" aria-label="Tournament tabs" style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 16 }}>
            <button type="button" role="tab" aria-selected={tabIndex === 0} onClick={() => onTabSelected(0)}>
              Résztvevők
            </button>
            {tournament.stages.map((stage, index) => (
              <button
                key={stage.id}
                type="button"
                role="tab"
                aria-selected={tabIndex === index + 1}
                onClick={() => onTabSelected(index + 1)}
              >
                {stage.name}
              </button>
            ))}
          </div>

          {tabIndex === 0 && (
            <div style={{ paddingInline: 100 }}>
              {tournament.tournament.participants.map((participant) => (
                <div key={participant.teamId}>
                  <h3 style={{ marginTop: 4, marginBottom: 4, maxWidth: '100%' }}>{participant.teamName}</h3>
                </div>
              ))}
            </div>
          )}

          {tournament.stages.map((stage, index) =>
            tabIndex === index + 1 ? (
              <div key={stage.id} style={{ overflowX: 'auto', scrollBehavior: 'smooth' }}>
                <KnockoutStage stage={stage} />
              </div>
            ) : null
          )}
        </div>
      </div>
    </CmschPage>
  )
}

export default Tournament

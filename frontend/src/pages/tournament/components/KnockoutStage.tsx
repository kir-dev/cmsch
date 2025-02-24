"use client"

import React, { useRef, useEffect, useState } from "react"
import { TournamentStageView, MatchView } from "../../../util/views/tournament.view.ts"
import Match from "./Match.tsx"
import BracketConnector from "./BracketConnector.tsx"

interface TournamentBracketProps {
  stage: TournamentStageView
}

interface MatchPosition {
  id: number
  x: number
  y: number
  width: number
  height: number
}

const TournamentBracket: React.FC<TournamentBracketProps> = ({ stage }) => {
  const [matchPositions, setMatchPositions] = useState<MatchPosition[]>([])
  const bracketRef = useRef<HTMLDivElement>(null)

  const matchesByLevel = stage.matches.reduce(
    (acc, match) => {
      if (!acc[match.level]) {
        acc[match.level] = []
      }
      acc[match.level].push(match)
      return acc
    },
    {} as Record<number, MatchView[]>
  )

  const levels = Object.keys(matchesByLevel)
    .map(Number)
    .sort((a, b) => a - b)
  const maxLevel = Math.max(...levels)

  useEffect(() => {
    if (bracketRef.current) {
      const positions: MatchPosition[] = []
      const matchElements = bracketRef.current.querySelectorAll<HTMLElement>('[data-match-id]')

      matchElements.forEach((el) => {
        const rect = el.getBoundingClientRect()
        const bracketRect = bracketRef.current!.getBoundingClientRect()
        positions.push({
          id: Number.parseInt(el.getAttribute('data-match-id') || '0', 10),
          x: rect.left - bracketRect.left + rect.width,
          y: rect.top - bracketRect.top + rect.height / 2,
          width: rect.width,
          height: rect.height
        })
      })

      setMatchPositions(positions)
    }
  }, [stage.matches])

  const getConnectorProps = (sourceId: number, targetId: number) => {
    const sourceMatch = matchPositions.find((m) => m.id === sourceId)
    const targetMatch = matchPositions.find((m) => m.id === targetId)

    if (!sourceMatch || !targetMatch) return null

    return {
      startX: sourceMatch.x,
      startY: sourceMatch.y,
      endX: targetMatch.x - targetMatch.width,
      endY: targetMatch.y
    }
  }

  const getMatchSpacing = (level: number) => {
    const baseSpacing = 4 // rem
    const spacingMultiplier = 2 ** (maxLevel - level)
    return `${baseSpacing * spacingMultiplier}rem`
  }

  return (
    <div className="p-4 overflow-x-auto">
      <h1 className="text-2xl font-bold mb-4">{stage.name}</h1>
      <div className="relative" ref={bracketRef}>
        <div className="flex">
          {levels.map((level) => (
            <div key={level} className="flex-shrink-0 mr-16">
              <h2 className="text-lg font-semibold mb-2">Round {level}</h2>
              <div style={{ display: 'flex', flexDirection: 'column', gap: getMatchSpacing(level) }}>
                {matchesByLevel[level].map(
                  match => (
                    <div key={match.id} data-match-id={match.id} className="relative">
                      <Match match={match} />
                    </div>
                  )
                )}
              </div>
            </div>
          ))}
        </div>
        {matchPositions.length > 0 &&
          stage.matches.map((match: { seed1: number; seed2: number; id: number }) => {
            if (match.seed1 < 0 || match.seed2 < 0) {
              const sourceId1 = match.seed1 < 0 ? -match.seed1 : match.seed1
              const sourceId2 = match.seed2 < 0 ? -match.seed2 : match.seed2
              const connectorProps1 = getConnectorProps(sourceId1, match.id)
              const connectorProps2 = getConnectorProps(sourceId2, match.id)

              return (
                <React.Fragment key={match.id}>
                  {connectorProps1 && <BracketConnector {...connectorProps1} />}
                  {connectorProps2 && <BracketConnector {...connectorProps2} />}
                </React.Fragment>
              )
            }
            return null
          })}
      </div>
    </div>
  )
}

export default TournamentBracket


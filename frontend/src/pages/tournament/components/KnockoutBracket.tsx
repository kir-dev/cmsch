import React from 'react'
import type { MatchTree } from '../util/matchTree'
import Match from './Match.tsx'

interface KnockoutBracketProps {
  tree: MatchTree
}

const lineColor = '#E2E8F0' // approximate Chakra gray.300

const KnockoutBracket: React.FC<KnockoutBracketProps> = ({ tree }: KnockoutBracketProps) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'row', position: 'relative' }}>
      {(tree.upperTree || tree.lowerTree) && (
        <>
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginRight: -8, marginBottom: 8 }}>
            {tree.upperTree && <KnockoutBracket tree={tree.upperTree} />}
            {tree.lowerTree && <KnockoutBracket tree={tree.lowerTree} />}
          </div>
          <div style={{ width: 40, marginRight: -8, position: 'relative' }}>
            {tree.upperTree && tree.lowerTree && (
              <>
                <div style={{ position: 'relative', top: '25%', left: 0, width: '50%', height: 2, background: lineColor }} />
                <div style={{ position: 'relative', top: '75%', left: 0, width: '50%', height: 2, background: lineColor }} />
                <div style={{ position: 'relative', top: '50%', left: '50%', width: '50%', height: 2, background: lineColor }} />
                <div style={{ position: 'relative', top: '22.5%', left: '50%', width: 2, height: '51.5%', background: lineColor }} />
              </>
            )}
            {tree.upperTree && !tree.lowerTree && (
              <div style={{ position: 'relative', top: '50%', left: 0, width: '100%', height: 2, background: lineColor }} />
            )}
            {!tree.upperTree && tree.lowerTree && (
              <div style={{ position: 'relative', top: '50%', left: 0, width: '100%', height: 2, background: lineColor }} />
            )}
          </div>
        </>
      )}
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <Match match={tree.root} />
      </div>
    </div>
  )
}

export default KnockoutBracket

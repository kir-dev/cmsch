import type React from "react"

interface BracketConnectorProps {
  startX: number
  startY: number
  endX: number
  endY: number
}

const BracketConnector: React.FC<BracketConnectorProps> = ({ startX, startY, endX, endY }) => {
  const midX = startX + (endX - startX) / 2

  return (
    <svg
      style={{
        position: "absolute",
        top: 0,
        left: 0,
        width: "100%",
        height: "100%",
        pointerEvents: "none",
      }}
    >
      <path d={`M${startX} ${startY} H${midX} V${endY} H${endX}`} fill="none" stroke="gray" strokeWidth="2" />
    </svg>
  )
}

export default BracketConnector


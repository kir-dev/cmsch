interface CircularProgressProps {
  value: number
  value2?: number
  size?: number
  strokeWidth?: number
  label: string
  label2?: string
  color?: string
  color2?: string
}

export const CircularProgress = ({
  value,
  value2 = 0,
  size = 160,
  strokeWidth = 10,
  label,
  label2,
  color = 'text-success',
  color2 = 'text-warning'
}: CircularProgressProps) => {
  const radius = (size - strokeWidth) / 2
  const circumference = 2 * Math.PI * radius
  const offset = circumference - (value / 100) * circumference
  const offset2 = circumference - ((value + value2) / 100) * circumference

  return (
    <div className="relative flex items-center justify-center" style={{ width: size, height: size }}>
      <svg className="h-full w-full -rotate-90 transform" width={size} height={size}>
        <circle
          className="stroke-border"
          strokeWidth={strokeWidth}
          stroke="currentColor"
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
        />
        {value2 > 0 && (
          <circle
            className={color2 + ' stroke-current'}
            strokeWidth={strokeWidth}
            strokeDasharray={circumference}
            style={{ strokeDashoffset: offset2 }}
            strokeLinecap="round"
            stroke="currentColor"
            fill="transparent"
            r={radius}
            cx={size / 2}
            cy={size / 2}
          />
        )}
        <circle
          className={color + ' stroke-current'}
          strokeWidth={strokeWidth}
          strokeDasharray={circumference}
          style={{ strokeDashoffset: offset }}
          strokeLinecap="round"
          stroke="currentColor"
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center text-center">
        <span className={label2 ? '-translate-y-2' : ''}>{label}</span>
        {label2 && (
          <>
            <div className="h-px w-2/3 bg-border my-1" />
            <span className="translate-y-2">{label2}</span>
          </>
        )}
      </div>
    </div>
  )
}

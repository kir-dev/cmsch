import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { useAltColor, useColor } from '@/util/color.utils.ts'
import type { ColorInstance } from 'color'

const TeamLabel = ({ name, color, desc }: { name: string; color?: string; desc?: string }) => {
  const myColor: ColorInstance = useColor(color)

  const [colorHex, darkColor] = [myColor.hex(), useAltColor(myColor).hex()]

  return desc ? (
    <LabelWithTooltip name={name} color={colorHex} darkColor={darkColor} desc={desc} />
  ) : (
    <LabelComponent name={name} color={colorHex} darkColor={darkColor} />
  )
}

const LabelComponent = ({ name, color, darkColor }: { name: string; color: string; darkColor: string }) => {
  return (
    <div
      className="rounded-xl border-2 px-3 py-0.5 text-[12px] font-bold tracking-[1.2px] w-fit h-fit"
      style={{
        backgroundColor: darkColor,
        borderColor: color,
        color: color
      }}
    >
      {name}
    </div>
  )
}

const LabelWithTooltip = ({ name, color, darkColor, desc }: { name: string; color: string; darkColor: string; desc: string }) => {
  return (
    <TooltipProvider>
      <Tooltip>
        <TooltipTrigger asChild>
          <div>
            <LabelComponent name={name} color={color} darkColor={darkColor} />
          </div>
        </TooltipTrigger>
        <TooltipContent>
          <p>{desc}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  )
}

export default TeamLabel

import { Emoji, EmojiStyle } from 'emoji-picker-react'
import { CSSProperties, useEffect, useState } from 'react'

interface AnimatedEmojiProps {
	emoji: string
	size?: number
	animation?: 'bounce' | 'pulse' | 'spin' | 'float'
	speed?: number
	style?: CSSProperties
}

export const AnimatedEmoji = ({
	emoji,
	size = 24,
	animation = 'bounce',
	speed = 2,
	style = {},
}: AnimatedEmojiProps) => {
	const [key, setKey] = useState(0) // Для принудительного ререндера

	// Анимации
	const animations = {
		bounce: `
      @keyframes bounce {
        0%, 100% { transform: translateY(0); }
        50% { transform: translateY(-15px); }
      }
      animation: bounce ${speed}s infinite;
    `,
		pulse: `
      @keyframes pulse {
        0%, 100% { transform: scale(1); }
        50% { transform: scale(1.2); }
      }
      animation: pulse ${speed}s infinite;
    `,
		spin: `
      @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }
      animation: spin ${speed}s linear infinite;
    `,
		float: `
      @keyframes float {
        0%, 100% { transform: translateY(0) rotate(0deg); }
        25% { transform: translateY(-10px) rotate(5deg); }
        75% { transform: translateY(-10px) rotate(-5deg); }
      }
      animation: float ${speed}s ease-in-out infinite;
    `,
	}

	// Принудительный ререндер при изменении свойств
	useEffect(() => {
		setKey(prev => prev + 1)
	}, [emoji, size, animation, speed])

	return (
		<div
			key={key}
			style={{
				display: 'inline-block',
				...style,
				cssText: animations[animation],
			}}
		>
			<Emoji
				unified={emoji}
				size={size}
				emojiStyle={EmojiStyle.APPLE} // Или TWITTER, GOOGLE и др.
			/>
		</div>
	)
}

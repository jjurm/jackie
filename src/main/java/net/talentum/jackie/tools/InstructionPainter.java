package net.talentum.jackie.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import net.talentum.jackie.moment.RobotInstruction;

public class InstructionPainter {

	public static BufferedImage paintOnImage(BufferedImage base, RobotInstruction instruction) {
		BufferedImage target = new BufferedImage(instruction.momentData.image.getWidth(),
				instruction.momentData.image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = target.getGraphics();

		/*-for (int y = 0; y < base.getHeight() - 10; y++) {
			for (int x = 0; x < base.getWidth() - 10; x++) {
				g.setColor(instruction.momentData.bw[x][y] ? Color.BLACK : Color.WHITE);
				g.fillRect(x, y, 1, 1);
			}
		}*/

		g.drawImage(base, -5, -5, null);
		drawPolyline(g, instruction.momentData.line, Color.red, 3);
		drawPolyline(g, instruction.momentData.bordersL, Color.green, 2);
		drawPolyline(g, instruction.momentData.bordersR, Color.green, 2);

		g.setColor(Color.BLUE);
		instruction.momentData.notFound.stream().forEach(p -> g.fillRect(p.x - 1, p.y - 1, 2, 2));

		g.setColor(Color.CYAN);
		instruction.momentData.highlight.stream().forEach(p -> g.fillRect(p.x - 2, p.y - 2, 4, 4));

		/*-g.setColor(Color.BLUE);
		for (Point p : instruction.momentData.notFound) {
			g.fillRect(p.x - 1, p.y - 1, 2, 2);
		}*/

		g.dispose();

		return target;
	}

	private static void drawPolyline(Graphics g, List<Point> points, Color color, int width) {
		if (points.size() == 0)
			return;
		g.setColor(color);
		Point a, b;
		b = points.get(0);
		for (int i = 1; i < points.size() - 1; i++) {
			a = points.get(i);
			g.drawLine(b.x, b.y, a.x, a.y);
			g.fillRect(a.x - (width / 2), a.y - (width / 2), width, width);
			b = a;
		}
	}

}
